#! /bin/bash

spark-submit \
    --master yarn \
    --py-files kdtree.py,kfold.py,point.py \
    --deploy-mode cluster\
    --name 'Experiment #01' \
    --num-executors 12 \
    --executor-cores 4 \
    --executor-memory 5G \
    experiment_01.py;
