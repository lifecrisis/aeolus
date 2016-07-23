#! /bin/bash

spark-submit \
    --master 'yarn' \
    --deploy-mode client \
    --py-files exclude.py,kdtree.py,kfold.py,point.py \
    --num-executors 12 \
    --executor-cores 4 \
    --executor-memory 7G \
    --name 'Experiment #03' \
    experiment_03a.py;
