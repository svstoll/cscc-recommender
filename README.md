# CSCC Recommender
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ch.uzh.ifi.ase%3Acscc-recommender&metric=alert_status)](https://sonarcloud.io/dashboard?id=ch.uzh.ifi.ase%3Acscc-recommender)

## Introduction
This repository contains an implementation of the CSCC recommender that was proposed in the paper [CSCC: Simple, Efficient, Context Sensitive Code Completion](https://ieeexplore.ieee.org/document/6976073) by Asaduzzaman et al. in 2014. The implementation is based on the [KaVE dataset](http://www.kave.cc) and is able to recommend method calls for method completion events of the [KaVE interaction dataset](http://www.kave.cc/datasets).

## Implementation Details and Differences
Contrary to the original implementation, we used only one index to store method invocations and their context. On a functional level this makes no difference, but using a single [Apache Lucene](https://lucene.apache.org/) index made handling and persisting the large amount of data of the KaVE datasets easier and faster.

Since the original paper did not state the cut-off value that was used to switch between the SimHash of the overall and the line contexts when ranking the recommendations, we used a value of 25 in our implementation. Additionally, we did not exlude recommendations if the normalized LCS distance of the overall contexts or the normalized Levenshtein distance of the line contexts dropped below a threshold of 0.3, because it excluded many correct recommendations and highly reduced the recall of the recommender.

## Usage
To receive recommendations from our implementation of the CSCC recommender, you must either generate your own index based on the [static repository dataset](http://www.kave.cc/datasets) of the KaVE project or download a prebuilt index [here](https://drive.google.com/open?id=1bnMhsoAovyORF4fsFRk22OEchkXaq1Fy). The prebuilt index has been generated using the complete static repository dataset. You can specify the index location used by the recommender in the `config.properties` file by adjusting the path of the `indexDirectory` key.

To generate your own index, you can use the class `MethodInvocationIndexer` to index all available context data (adjust the path of the `contextDirectory` key in the `config.properties` file). 

Based on the currently specified index, the class `CsccRecommender` can be used receive method recommendations for method completion event of the [KaVE interaction dataset](http://www.kave.cc/datasets).

This repository containts two runnable classes that also serve as an example usage of the recommender. Running the class `CompletionEventEvaluator` with the argument `-index` will clear the index in currently specified index location (`indexDirectory` in `config.properties`), generate a new index based on all available context data (`contextDirectory` in `config.properties`) and perform an evalution with this index based on all available applied method completion events (`eventsDirectory` in `config.properties`). Running this class without `-index` will skip the index generation and use the currently specified index. The results (recall, precision, average recommendation time, etc.) will be stored in the specified results folder (`resultsDirectory` in `config.properties`).

By running the class `CrossProjectEvaluator`, you can evaluate the performance of the recommender without the need of completion events. This evaluation will split the projects of the static repository dataset into a given number of groups (provide an integer that is smaller than the number of provided projects as the first program argument, default is 2). Then it will build a new index based on all but one group and use the remaining group to evaluate the recommender by performing recommendations on all method invocations within that group. The results will also be stored in the `resultsDirectory` specified in the `config.properties` file.

### Maven Dependency
You can add this implementation of the CSCC recommender as a Apache Maven dependency in your own project:
```xml
<dependencies>
    <!-- Other dependencies ... -->
    <dependency>
        <groupId>com.github.svstoll</groupId>
        <artifactId>cscc-recommender</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

## Setting up the project
1. Download KaVE data set from www.kave.cc/datasets. Download the context dataset and put it in ./data/contexts,
then download the event dataset and put it in ./data/events. Or you can modify the config.properties file corresponding to the
location of context dataset and event dataset on your machine.
2. Check com.github.svstoll.csccrecommender.evaluation.CompletionEventEvaluator and run this class to trigger the code completion
on the event dataset and receive the result as TopK precision as well as some other statistics.

## Future Improvement
Currently, the model can only recommend for Method. So Future Improvement could be adding recommendations for Property, Field, etc.

## Implementation Difference 
1. Value to switch between hamming distance for overall and line context not stated in paper.
2. Droping if normalized distance metric below 0.3 leads to poor recall. So it's removed in the implementation.

## Evaluation
In the original paper, the authors focused on two APIs, SWT and Swing/AWT. They chose 4 projects for each API and performed the evaluation from the following perspective:
1. Ten-fold cross-validation. Doesn't care if it's cross-project recommendation or within-project recommendation. 
2. What if parent node expects a particular type?
3. Will the performance differ for different AST node types?
4. Will target methods calls within extension methods impose any challenge to the code completion technique?
5. Indexing time and code completion time.
6. CSCC can capture context, so will different types of context give different result?
7. What about cross-project recommendations?

In contrast to the original evaluation, we had access to real world code completion usages thanks to the KaVE interaction dataset. Mainly based on this data, we carried out the following types of evaluations:

1. Performance for completion events using the full index.
2. Performace for methods calls within extension methods (using completion events and full index)
3. Performace for methods calls within NONE extension methods (using completion events and full index)
4. Indexing time and average recommendation time (using completion events and full index)
5. Cross-project evaluation with the KaVE static repository dataset.

To reproduce these evaluations, check out the section before.

### KaVE Interaction Dataset Results
The following results are based on the complete KaVE interaction dataset (18.01.2018). We only considered method completion events that have actually been applied and used all data from the KaVE static repository dataset to generate the underlying index.

Recommendations requested: 4324
Recommendations made: 1502
Average recommendation time (ms): 18.74

Overall recall: 0.347
Top-1 precision: 0.174
Top-3 precision: 0.346
Top-10 precision: 0.455

Recommendations requested within extension methods: 209
Recommendations made within extension methods: 98
Overall recall within extension methods: 0.469
Top-3 precision within extension methods: 0.5

Recommendations requested NONE within extension methods: 4115
Recommendations made within NONE extension methods: 1404
Overall recall within NONE extension methods: 0.341
Top-3 precision within NONE extension methods: 0.335

### Notes
We calculated recall, precision and F-measure the same way as in the evaluation of the original paper see (p. 75). Therefore recall will be the same for for top-1, top-3 and top-10 recommendations (see also table 1, p. 76).
