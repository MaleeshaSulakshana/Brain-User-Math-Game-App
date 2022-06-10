package com.mind.mind_calc.Question;

public class LevelHard extends Question {

    @Override
    public String generateQuestion() {

        GenerateMaths generateMaths = new GenerateMaths();

        int count = 100;
        String[] questionList = new String[count];
            for (int i=0; i< count; i++) {

                String[] numbers = new String[4];
                for (int x=0; x< 4; x++) {
                    numbers[x] = generateMaths.numbers(01, 99);
                }

                String[] conditions = new String[3];
                for (int y=0; y< 3; y++) {
                    conditions[y] = generateMaths.condition();
                }

                String question = numbers[0]+" "+
                        conditions[0]+" "+
                        numbers[1]+" "+
                        conditions[1]+" "+
                        numbers[2]+" "+
                        conditions[2]+" "+
                        numbers[3];

                questionList[i] = question;
            }

        String randomQuestion = generateMaths.randomQuestion(questionList);
        return randomQuestion;

    }

}