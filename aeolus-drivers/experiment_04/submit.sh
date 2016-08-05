#! /bin/bash

spark-submit \
    --master 'yarn' \
    --deploy-mode client \
    --py-files exclude.py,kdtree.py,kfold.py,point.py \
    --num-executors 14 \
    --executor-cores 4 \
    --executor-memory 7G \
    --name 'Experiment #03B' \
    experiment_03b.py;
