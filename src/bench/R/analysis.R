data <- read.csv("benchmark_log.csv")

full <- lm(I(totalTime - numTasks*taskTime) ~
             0 +
             numWorkers +
             I(numTasks*messageSize) +
             I(numTasks*messageSize/numWorkers) +
             I(numTasks*taskTime) +
             I(numTasks*taskTime/numWorkers),
           data
           )
summary(full)

reduced <- lm(I(totalTime - numTasks*taskTime) ~ 0 +
                I(numTasks*messageSize/numWorkers) +
                I(numTasks*taskTime) +
                numWorkers,
              data
)

summary(reduced)

anova(full, reduced)

min <- lm(totalTime ~ 0 +
            I(numTasks*taskTime/numWorkers)
           + I(messageSize*numTasks)
            # I(1/(numWorkers/(numTasks*messageSize*taskTime)))
          , data)
summary(min)

more <- lm(totalTime ~
             I(numTasks*taskTime/numWorkers)
           + I(messageSize*numTasks)
           , data)
summary(more)

anova(min, more)
