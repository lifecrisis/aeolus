#! /bin/bash

spark-submit \
    --master yarn \
    --py-files kdtree.py,kfold.py,point.py \
    --deploy-mode client \
    --name 'Experiment #02' \
    --num-executors 12 \
    --executor-cores 4 \
    --executor-memory 7G \
    experiment_02.py;
