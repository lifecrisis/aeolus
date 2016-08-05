#! /bin/bash


# Take clean-up and preparation actions.
if [ ! -d "partitions/" ]; then
    mkdir partitions/
    python partition.py
fi
test -d 'results/' && rm -rf results/
test -f 'results.csv' && rm results.csv


spark-submit \
    --master 'local[*]' \
    --name 'Experiment #04' \
    --py-files kdtree.py,kfold.py,point.py \
    experiment_04.py;


# spark-submit \
#     --master 'yarn' \
#     --name 'Experiment #04' \
#     --deploy-mode client \
#     --py-files kdtree.py,kfold.py,point.py \
#     --num-executors 14 \
#     --executor-cores 4 \
#     --executor-memory 7G \
#     experiment_04.py;
