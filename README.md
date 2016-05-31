# aeolus

## Introduction

This repository holds the code base from my research assistantship in the 
spring and summer of 2016. Our goal was to develop software that could 
generate local estimates of atmospheric pollution levels for users in the
United States. 

Initially, the project consisted only of the `aeolus-parent` directory, which
held two subdirectories comprising the entire code base. During the first 
five months of development, the project was not under version control. As
someone who had only been writing software for a year, I didn't see the value
in using git until I had more experience.

The results of this work will soon be published in an official journal.

## Project Structure

The project structure is simple to understand. The folders are arranged as
follows:

* `aeolus-parent` is the directory containing the entirity of the code that
was written prior to my switch to Python. This portion of the project is
written in Java.
* `aeolus-script` consists of Python scripts that I wrote to clean and format
data prior to processing.
* `aeolus-driver` consists of Python driver applications for the 
[Apache Spark](http://spark.apache.org/) cluster at my university.
* `data` is the directory containing the datasets under consideration in this
project.

## Acknowledgements

I would like to thank Professors Li, Tong, and Zhou of Georgia Southern
University for the chance to work on this project and support their research
efforts.
