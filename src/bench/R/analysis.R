data <- read.csv("benchmark_log.csv")

full <- lm(totalTime ~
             1 +
             numWorkers +
             I(numIterations*messageSize) +
             I(numIterations*messageSize/numWorkers) +
             I(numIterations*taskTime) +
             I(numIterations*taskTime/numWorkers),
           data
           )
summary(full)

reduced <- lm(totalTime ~
                I(numIterations*messageSize/numWorkers) +
                I(numIterations*taskTime/numWorkers) +
                numWorkers,
              data
)

summary(reduced)

anova(full, reduced)
