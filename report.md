# Report for assignment 3

## Project

Name: Javaparser

URL: https://github.com/javaparser/javaparser 

JavaParser is a tool for analyzing java code by providing you with an Abstract Syntax Tree.

## Onboarding experience

The README for our repository specified to compile the source code with the command “mvnw clean install”, however this runs a file inside the repository, meaning we had to change the command to “./mvnw clean install” for it to find the file. After that the project could be built successfully and all the tests pass.

The README also specified that maven is used, and that had to be installed before building the project.

## Complexity

1. What are your results for five complex functions?
   * Did all methods (tools vs. manual count) get the same result?
   * Are the results clear?

Tianxing Wu: MethodResolutionLogic::inferTypes@893-945@./javaparser-core/src/main/java/com/github/javaparser/resolution/logic/MethodResolutionLogic.java
Tools: 25 Manual: 25 Yes, they are the same
Very clear

John Söderholm:
MethodResolutionLogic::findMostApplicable@572-669@./javaparser-core/src/main/java/com/github/javaparser/resolution/logic/MethodResolutionLogic.java
Tools: 23 Manual: 23
It was quite hard to keep track of calculate the cyclomatic complexity using a control-flow graph, but the results were still fairly clear. 


Tobias Hansson:
MethodResolutionLogic::isApplicable@420-544@./javaparser-core/src/main/java/com/github/javaparser/resolution/logic/MethodResolutionLogic.java
Tools: 27, Manual 27.
The initial count differed but after learning how lizard counts everything matched.


Angelica Engström:
MethodResolutionLogic::isApplicable@79-203@./javaparser-core/src/main/java/com/github/javaparser/resolution/logic/MethodResolutionLogic.java
Tools: 37 Manual: E=66 and N=48, thus E-N+2*P=20
They didn’t get the same result, the tool may have identified areas of the code that contribute more to complexity than I did manually. It is still unclear why the results differ that much. However, both of the methods concluded a complex function. But it was hard to measure it manually since there were around 120 lines of code.

Celina Tjärnström:
MethodResolutionLogic::isMoreSpecific@692-797@./javaparser-core/src/main/java/com/github/javaparser/resolution/logic/MethodResolutionLogic.java
Tools: 41 Manual: 33
The complexity differ slightly, it seems like the lizard tool counts certain keywords which is why the result is a bit different to a control flow chart. I could manually count to the same number now when I know how the tool functions so the result is clear.

2. Are the functions just complex, or also long?

Tianxing Wu: It is very long and complex

John Söderholm: It is very long and quite complex.

Angelica Engström: It is very long and complex

Celina Tjärnström: It is complex and long.

Tobias Hansson: Both
 
3. What is the purpose of the functions?

Tianxing Wu: the purpose is to try to infer types for a given source and target.

Angelica Engström: The purpose is to check if the given method declaration is applicable with a value that has been used as a search/query term

Celina Tjärnström: The purpose is to look at and compare method declarations and evaluate which is more specific, where variadic functions are less specific than others.

John Söderholm: The purpose of the function is to find the most applicable in a list of method declarations given a set of additional parameters. 

Tobias Hansson: The purpose of the function is the same as for Angelica, we picked functions that take input as different types, so they do things differently but we did not realise this until later.

4. Are exceptions taken into account in the given measurements?

Tianxing Wu: There is no exception in this function.

Angelica Engström: There is no exception in this function.

Celina Tjärnström: There is no exception in this function.

John Söderholm: There is one direct exception in the function. It has been taken into account. 

Tobias Hansson: There is one exception that was taken into account.

5. Is the documentation clear w.r.t. all the possible outcomes?

Tianxing Wu: There is no document for this function. It does not return anything, so it must have unknown side effects.

John Söderholm: The documentation for this function primarily consists of in-line comments that are written before some of the conditional statements. However, the function does not have complete documentation in the form of JavaDoc. 

Angelica Engström: The documentation for this function primarily consists of in-line comments that are written before some of the conditional statements. It also utilizes JavaDoc but it’s not 100% clear what is actually happening.

Celina Tjärnström: The documentation for this function primarily consists of in-line comments that are written before some of the conditional statements. It does not use JavaDoc comments.

Tobias Hansson: The java docs are unclear and confusing and do not tell what the function does. There are a lot of line comments that clarify what individual lines do, but it is still not explained what the whole function does.

## Refactoring

Plan for refactoring complex code:

Tianxing Wu: Use Maybe functor (In java it is called Optional) to eliminate if statements.

John Söderholm: Extracting complex if-statements into distinct functions. This would reduce the cyclomatic complexity significantly. One of these functions would have the responsibility to investigate and handle possible null parameters in the list of input methods.

Angelica Engström: Extracting complex branches into distinct functions or classes. Using design patterns?

Celina Tjärnström: To lower the cyclomatic complexity you could refactor it into smaller functions. The responsibility of the function is to compare method declarations with varying numbers of parameters so a lot of the decisions are needed to exhaustively check every condition. There are some helper functions, but some additional ones for the most nested if statements could lower complexity and improve readability. A potential drawback could be splitting it into too many small functions making worse code.

Tobias Hansson: Some for loops could possibly be their own helper functions. Also, some validation is done on the input, this could be put in a helper function.

Estimated impact of refactoring (lower CC, but other drawbacks?).

Tianxing Wu: CC will be lower by around 50%. Drawback: Java’s Optional class is not an instance of Alternative so a helper function is needed.

John Söderholm: Refactoring would make the function a lot less complex. CC will be lowered by approximately 45%.  

Tobias hansson: Refactoring could definitely make the function less complex and more readable/understandable. A simple refactoring should not have any drawbacks in this case.

Angelica Engström: What they said

Carried out refactoring (optional, P+):

Tianxing Wu: git diff 8540ee1185cc2beb477fc78a8355e76ccba6a756~ 8540ee1185cc2beb477fc78a8355e76ccba6a756

Angelica Engström: git diff 9f88d932917ce4d6339c2ddd842d20ca4da0f50e~ fda056b6a4f119853a5875a5c3fd0abaf9b26114

## Coverage

### Tools

Document your experience in using a "new"/different coverage tool.

How well was the tool documented? Was it possible/easy/difficult to
integrate it with your build environment? 

Tobias Hansson: I have used code coverage in java and python before without any problems. For this project however, the process was painful. The build tools used for the 160-something thousand lines of code were impossible to integrate with my IDE. Some tests are skipped for no reason, specifically in the file i sought to improve. In others words. The tools should not be difficult to use but in this case they were.

John Söderholm: I have used a couple of code coverage tools in Java and Python previously. They have also worked perfectly fine after some initial setup, but that was not the case in this project. I tried all of the recommended tools, but I never seemed to get it working properly. There were some lines of code that were skipped for absolutely no reason. Therefore, the process of integrating a code coverage tool was surprisingly painful. 

Angelica Engström: It was pretty hard to integrate it with our build process since there wasn’t alot of documentation to build the project with our IDE.

### Your own coverage tool

Show a patch (or link to a branch) that shows the instrumented code to
gather coverage measurements.

The patch is probably too long to be copied here, so please add
the git command that is used to obtain the patch instead:

Tianxing Wu: git diff 92cdb7c1690c9adbeba45103eb5d87b07ee3266e~ 92cdb7c1690c9adbeba45103eb5d87b07ee3266e

What kinds of constructs does your tool support, and how accurate is
its output?

Tianxing Wu: Works for any tests.

Angelica Engström: Works for any branches within the function.

### Evaluation

1. How detailed is your coverage measurement?

Tianxing Wu: Every branch is measured.

2. What are the limitations of your own tool?

Tianxing Wu: Can not accumulate among different tests.

3. Are the results of your tool consistent with existing coverage tools?

Tianxing Wu: Yes.

## Coverage improvement

With the tool Jacoco we could see that none of our functions were tested originally which means they started out with 0% coverage. Every new added test increased coverage without interfering with the old tests.

Test cases added:

Tianxing Wu: git diff 05f3b2725f6b98df4bd35e8f148ec746f378cf4e~ 05f3b2725f6b98df4bd35e8f148ec746f378cf4e

Number of test cases added: two per team member (P) or at least four (P+).

Tianxing Wu: 4

Angelica Engström: 4

Tobias Hansson: 2

John Söderholm: 4

Celina Tjärnström: 3

## Self-assessment: Way of working

The self-assessment was unanimous. Over time, we have seen further improvements in our way of communicating with each other. Our way of continually tuning our use of practices and tools has some potential for improvement.

### Seeded

- [x] The team mission has been defined in terms of the opportunities and outcomes.
- [x] Constraints on the team's operation are known.
- [ ] Mechanisms to grow the team are in place.
- [x] The composition of the team is defined.
- [x] Any constraints on where and how the work is carried out are defined.
- [x] The team's responsibilities are outlined.
- [x] The level of team commitment is clear.
- [x] Required competencies are identified.
- [x] The team size is determined.
- [x] Governance rules are defined.
- [x] Leadership model is determined.

### Formed

- [x] Individual responsibilities are understood.
- [x] Enough team members have been recruited to enable the work to progress.
- [x] Every team member understands how the team is organized and what their individual role is.
- [x] All team members understand how to perform their work.
- [x] The team members have met (perhaps virtually) and are beginning to get to know each other.
- [x] The team members understand their responsibilities and how they align with their competencies.
- [x] Team members are accepting work.
- [x] Any external collaborators (organizations, teams and individuals) are identified.
- [x] Team communication mechanisms have been defined.
- [x] Each team member commits to working on the team as defined.

### Collaborating

- [x] The team is working as one cohesive unit.
- [x] Communication within the team is open and honest.
- [x] The team is focused on achieving the team mission.
- [x] The team members know and trust each other.

### Performing

- [x] The team consistently meets its commitments.
- [x] The team continuously adapts to the changing context.
- [x] The team identifies and addresses problems without outside help.
- [x] Effective progress is being achieved with minimal avoidable backtracking and reworking.
- [x] Wasted work and the potential for wasted work are continuously identified and eliminated.

### Adjourned

- [x] The team responsibilities have been handed over or fulfilled.
- [x] The team members are available for assignment to other teams.
- [x] No further effort is being put in by the team to complete the mission.




