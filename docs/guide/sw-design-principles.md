# SW design principles

## Make it simple
* watch [Simple made easy](https://www.infoq.com/presentations/Simple-Made-Easy) 
* read [12-factor apps](https://12factor.net/)

## Fighting tech debt

There are three levels of tech debt. If you fix higher level it has way bigger impact than fixing the lower.

Levels of tech debt:
1) highest level is overall platform architecture (how the apps communicate, how they are deployed, how to do logging/monitorig etc.)
2) middle level is architecture of a single app - layers of app, if we use streams or actors and what are our policies to use them etc.
3) and the lowest level is actual code in new features (which is still important but has the least effect) 

How to fix them:
1) Discuss and confront overall architecture and deployments. Should be company wide initiative
2) Should be solved per app. There are some general guidelines which can be shared between projects tough
3) The code principles can also be shared. Probably except how to use the of libraries as product engineering team team for sure use different libs than system engineering or machine learning team. level 3) can be attacked in code reviews. also level 2) can be attacked in code reviews but first it has to be written somewhere