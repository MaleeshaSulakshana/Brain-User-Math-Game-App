package com.mind.mind_calc.Question;

public class LevelEasy extends Question {

    @Override
    public String generateQuestion() {
        GenerateMaths generateMaths = new GenerateMaths();

        int count = 100;
        String[] questionList = new String[count];
        for (int i=0; i< count; i++) {

            String[] numbers = new String[2];
            for (int x=0; x< 2; x++) {
                numbers[x] = generateMaths.numbers(01, 99);
            }

            String condition = generateMaths.condition();
            String question = numbers[0]+" "+condition+" "+numbers[1];

            questionList[i] = question;
        }

        String randomQuestion = generateMaths.randomQuestion(questionList);
        return randomQuestion;
    }

}
