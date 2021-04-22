library(tidyverse)

data <- read.csv("benchmark_log.csv")
data$numTasks = as.double(data$numTasks)
data$java = as.integer(as.logical(data$java))

full <- lm(totalTime ~
             numWorkers +
             I(numTasks*messageSize) +
             I(numTasks*messageSize/numWorkers) +
             I(numTasks*taskTime) +
             I(numTasks*taskTime/numWorkers),
           data
           )
summary(full)

reduced <- lm(totalTime ~ 0 +
            I(numTasks*taskTime/numWorkers) +
            I(numTasks*messageSize)
          , data)
summary(reduced)

anova(full, reduced)

red_factor <- lm(totalTime ~ 0 +
                   I(numTasks*taskTime/numWorkers) +
                   I(numTasks*messageSize) +
                   java
          , data)
anova(red_factor, reduced)


pred = predict(reduced, data, interval="prediction")
minPredicted = mutate(data, fit = pred[, "fit"]/1000, lwr = pred[, "lwr"]/1000, upr = pred[, "upr"]/1000)

sincpt <- minPredicted[
  data$numTasks == 1000000 & data$caseDescription == "str2et -> getfov -> sincpt (very long)",
  c("numWorkers", "taskTime", "totalTime", "fit", "lwr", "upr")
]
sincpt$totalTime = sincpt$totalTime/1000
singleTime <- sincpt[1, "taskTime"] * 1000
sincpt <- rbind(sincpt, c(1, singleTime, singleTime, singleTime, singleTime, singleTime))

ggplot(sincpt) +
  scale_colour_manual(name="",
                      values=c(actual="black", predicted="blue", ideal="red")) +
  geom_point(aes(numWorkers, totalTime, color="actual"), size=2) +
  geom_point(aes(numWorkers, fit, color="predicted"), shape=15) +
  geom_errorbar(aes(numWorkers, fit, ymin = lwr, ymax = upr, color="predicted", width=0.5)) +
  stat_function(fun=function(x) singleTime / x, aes(color="ideal")) +
  
  ylim(0, 60) +
  xlab("Number of Workers") +
  ylab("Running Time (sec)") +
  ggtitle("Running sincpt 1,000,000 times")

