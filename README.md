# CSCC Recommender

No documentation, yet.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ch.uzh.ifi.ase%3Acscc-recommender&metric=alert_status)](https://sonarcloud.io/dashboard?id=ch.uzh.ifi.ase%3Acscc-recommender)

### Evaluation
Like in the indexing part, specify the location for context dataset, interaction dataset, and where to store indexed files.
#### Evaluate with KaVE Context Dataset
Need to specify crossValidationNum, and then run in Evaluator4Contexts. Each time the results will be stored in a new csv file. <br>
The indexing part is pretty fast. But if the context dataset is split into 5 subgroups, all recommendations for one subgroup will take roughly 30 hours. So in the program, we only run for one iteration(with currectItr = 0). <br>
If there is no need to reindex, can comment out "indexAllAvailableContexts". <br>
The recall and precision formulas are from the paper. With that formula for calculating recall, the recall will be the same for TOP 1,TOP3,TOP10 recommendations. It's confusing but the Recalls in TABLE 1 for CSCC method in their paper confirm that they are calculating like this way.

#### Evaluate with KaVE Interaction Dataset
Before calling the CompletionEventEvaluator, make sure indexed files are already stored in the location, specified in config.properties.
