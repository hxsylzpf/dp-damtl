# Privacy-Preserving Distributed Multi-Task Learning with Asynchronous Updates
Liyang Xie, Inci M. Baytas, Kaixiang Lin, Jiayu Zhou

Accepted by SIGKDD 2017

## Abstract
Many data mining applications involve a set of related learning tasks. 
Multi-task learning (MTL) is a learning paradigm that improves generalization
performance by transferring knowledge among those tasks. MTL 
has attracted so much attention in the community, and various algorithms
have been successfully developed. Recently, distributed MTL has also been 
studied for related tasks whose data is distributed across different geographical 
regions.
One prominent challenge of the distributed MTL frameworks is to maintain the privacy of the data. 
The distributed data may contain sensitive and private information such as patient records and registers of a company. In such cases, distributed MTL frameworks are required to preserve the privacy of the data. 
In this paper, we propose a novel privacy-preserving distributed MTL framework to address this
challenge. A privacy-preserving proximal gradient algorithm, which asynchronously 
updates models of the learning tasks, is introduced to solve a general class of MTL formulations. 
The proposed asynchronous approach is robust against network delays and provides a guaranteed 
differential privacy through carefully designed perturbation.
Theoretical guarantees of the proposed algorithm are derived and supported by the extensive experimental results. 

## Acknowledgement
This research is supported in part by the Office of Naval Research (ONR) under
grant number N00014-17-1-2265, N00014-14-1-0631 and National Science Foundation under Grant
IIS-1565596, IIS-1615597. 
