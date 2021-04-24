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
            I(numTasks*taskTime) +
            I(numTasks*messageSize)
          , data)
summary(reduced)

anova(full, reduced)

red_factor <- lm(totalTime ~ 0 +
                   I(numTasks*taskTime/numWorkers) +
                   I(numTasks*taskTime) +
                   I(numTasks*messageSize) +
                   java
          , data)
anova(red_factor, reduced)

pred = predict(full, data, interval="prediction")
predicted = mutate(data, fit = pred[, "fit"]/1000, lwr = pred[, "lwr"]/1000, upr = pred[, "upr"]/1000)

sincpt <-predicted[
  data$numTasks == 1000000 & data$caseDescription == "str2et -> getfov -> sincpt (very long)",
  c("numWorkers", "taskTime", "totalTime", "fit", "lwr", "upr")
]
sincpt$totalTime = sincpt$totalTime/1000
sincptSingleTime <- sincpt[1, "taskTime"] * 1000
# sincpt <- rbind(sincpt, c(1, singleTime, singleTime, singleTime, singleTime, singleTime))

ggplot(sincpt) +
  scale_colour_manual(name="",
                      values=c(Actual="black", Predicted="blue", Ideal="red")) +
  geom_point(aes(numWorkers, fit, color="Predicted"), shape=15) +
  geom_errorbar(aes(numWorkers, fit, ymin = lwr, ymax = upr, color="Predicted", width=0.5)) +
  geom_point(aes(numWorkers, totalTime, color="Actual"), size=2) +
  stat_function(fun=function(x) sincptSingleTime / x, aes(color="Ideal")) +
  ylim(0, 51) +
  xlab("Number of Workers") +
  ylab("Running Time (sec)") +
  ggtitle("Running SINCPT 1,000,000 times")

ints <- predicted[
  data$numTasks == 1000000 & data$caseDescription == "output 1000 integers",
  c("numWorkers", "taskTime", "totalTime", "fit", "lwr", "upr")
]
ints$totalTime  = ints$totalTime/1000
intSingleTime <- ints[1, "taskTime"] * 1000
# ints <- rbind(ints, c(1, singleTime, singleTime, singleTime, singleTime, singleTime))

ggplot(ints) +
  scale_colour_manual(name="",
                      values=c(Actual="black", Predicted="blue", Ideal="red")) +
  geom_point(aes(numWorkers, fit, color="Predicted"), shape=15) +
  geom_errorbar(aes(numWorkers, fit, ymin = lwr, ymax = upr, color="Predicted", width=0.5)) +
  geom_point(aes(numWorkers, totalTime, color="Actual"), size=2) +
  stat_function(fun=function(x) intSingleTime / x, aes(color="Ideal")) +
  xlab("Number of Workers") +
  ylab("Running Time (sec)") +
  ggtitle("Outputing 1000 ints, 1,000,000 times")
