data <- read.csv("benchmark_log.csv")

full <- lm(I(totalTime - numIterations*taskTime) ~
             0 +
             numWorkers +
             I(numIterations*messageSize) +
             I(numIterations*messageSize/numWorkers) +
             I(numIterations*taskTime) +
             I(numIterations*taskTime/numWorkers),
           data
           )
summary(full)

reduced <- lm(I(totalTime - numIterations*taskTime) ~ 0 +
                I(numIterations*messageSize/numWorkers) +
                I(numIterations*taskTime) +
                numWorkers,
              data
)

summary(reduced)

anova(full, reduced)

min <- lm(totalTime ~ 0 +
            I(numIterations*taskTime/numWorkers)
           + I(messageSize*numIterations)
            # I(1/(numWorkers/(numIterations*messageSize*taskTime)))
          , data)
summary(min)

more <- lm(totalTime ~
             I(numIterations*taskTime/numWorkers)
           + I(messageSize*numIterations)
           , data)
summary(more)

anova(min, more)
