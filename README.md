# CSCC Recommender

## Introduction
We implemented the CSCC Recommender in the paper CSCC: Simple, Efficient, Context Sensitive Code Completion, by Muhammad A., Chanchal K., Roy Kevin A., Schneider Daqing H. Then carried out the evaluation using KaVE Context Dataset and KaVE Interaction Dataset, http://www.kave.cc.
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ch.uzh.ifi.ase%3Acscc-recommender&metric=alert_status)](https://sonarcloud.io/dashboard?id=ch.uzh.ifi.ase%3Acscc-recommender)

## Evaluation
In the original paper, the authors focused on two API's, SWT, Swing/AWT. They choose 4 projects for each API. And they gave the evaluations from the following perspective:
1. Ten-fold cross-validation. Doesn't care if it's cross-project recommendation or within-project recommendation. 
2. What if parent node expects a particular type?
3. Will the performance differ for different AST node types?
4. Will target methods called from within extension methods impose any challenge to the code completion techniques?
5. Indexing time and code completion time.
6. CSCC can capture context, so will different types context give different result?
7. What about cross-project recommendation? <br>

Their evaluation can mostly be categorized as static analysis, since the projects they use are already finished. So it's very different from the real cases.
In comparison, the KaVE Context Dataset is much bigger, containing 309 projects, and KaVE Interaction Dataset is real world cases, recorded from daily usage of the developers. So we can carry out much bigger analysis and try to see what the result will be if this is deployed, not just simulation results.
In summary, we carried out:
1. Cross-validation for whole KaVE Context Dataset.
2. Indexing time and code completion time.
3. Cross-project recommendation.
4. Performace for methods called from within extension methods.
5. Performance under daily usage of developers.

### Findings
1. Indexing for all Kave Context Dataset is pretty fast, roughly only one hour. But since we indexed more files, the query each time for our model, 18ms, is slower than the original report, around 2-3ms. But it's still acceptable.
2. In the paper, they find out that performace for methods called from within extension methods is worse. But in our evaluation, it's actually better. 
3. In the paper, they has good results for all TOP1, TOP3, TOP10 recommendations. But the performance varies much. Generally, we didn't find performance under TOP1 and TOP3 recommendations very good. 
4. When we use the same evaluation procedure, that is, cross-validation for all KaVE Context Dataset, precision for TOP10 recommendations are nearly the same as the authors' ten-fold cross-validation. But the Recall is much lower. It's not surprising since KaVE Context Dataset is much bigger and we are not only focusing on two API's.
5. Following the paper's formulas to compute Precision and Recall, the results for Recall under TOP1, TOP3, TOP10 are always the same. This is a little unexpected. Actually, at first, the formula we used is not the same as theirs. Only after one discussion, we switched to their formula.
6. To use KaVE Completion Dataset, each time when there is a completion event, if the user chooses to apply the suggestion, then we know the ground truth. So after this filtering, we only have 4324 queries. And under those real world usage, the precision is almost 0.6. We find it very impressive. Since we are not concerning about which API at all, what kind of users, what project they are working on, and there can be a huge gap between the projects in KaVE Context Dataset and the projects in KaVE Completion Dataset, we were worried that the result will be unacceptable. But the precision turned out to be 0.6, which is really impressive. Although the Recall here is very low, less than 0.4. But when people want to use CSCC, they will also select similar or even the older projects they worked on to index, so Recall can be higher.

### Details
1. The indexing part is pretty fast. But if the context dataset is split into 5 subgroups, all recommendations for one subgroup will take roughly 30 hours. So in the program, each time we only run for one iteration(with currectItr = 0). If want to run for all iterations, need to change function "trainAndEvaluateOnContextDataset".
2. The recall and precision formulas are from the paper, equations (1)-(3). With that formula for calculating recall, the recall will be the same for TOP 1,TOP3,TOP10 recommendations. It's confusing but the Recalls in TABLE 1 for CSCC method in their paper confirm that they are calculating like this way.

### How to run
Like in the indexing part, specify the location for context dataset, interaction dataset, and where to store indexed files in config.properties.
#### Evaluate with KaVE Context Dataset
Need to specify crossValidationNum, and then run in Evaluator4Contexts. Each time the results will be stored in a new csv file.
If there is no need to reindex, can comment out "indexAllAvailableContexts".
#### Evaluate with KaVE Interaction Dataset
Before calling the CompletionEventEvaluator, make sure indexed files are already stored in the correct location, specified in config.properties.
