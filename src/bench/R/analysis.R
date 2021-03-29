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

data$response <- data$totalTime
min <- lm(response ~ 0 +
            I(numIterations*taskTime/numWorkers)
           + I(messageSize*numIterations)
            # I(1/(numWorkers/(numIterations*messageSize*taskTime)))
          , data)
summary(min)
plot(min)
