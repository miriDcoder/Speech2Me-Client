package com.example.project;

import java.util.ArrayList;

public class QuestionsData {
    ArrayList<ArrayList<Question>> questionsData = new ArrayList<ArrayList<Question>>();
    ArrayList<Question> questionsLevel1 = new ArrayList<Question>();
    ArrayList<Question> questionsLevel2 = new ArrayList<Question>();
    private int mSizeOfLevel1;
    private int mSizeOfLevel2;

    public void makeQuestionList() {
        //Level 1 questions
        questionsLevel1.add(new Question("Limon", "https://imagizer.imageshack.com/img921/9784/NzTcE7.jpg", "boker_tov", 1, 0));
        questionsLevel1.add(new Question("Tut", "https://imagizer.imageshack.com/img922/2140/4UO1Ae.png", "boker_tov", 1, 1));
        questionsLevel1.add(new Question("Tsav", "https://imagizer.imageshack.com/img921/7118/MC0ODM.png", "boker_tov", 1, 2));
        questionsLevel1.add(new Question("Tinok", "https://imagizer.imageshack.com/img924/1850/Bx2dNE.png", "boker_tov", 1, 3));
        questionsLevel1.add(new Question("Balon", "https://imagizer.imageshack.com/img924/391/47dmi5.png", "boker_tov", 1, 4));
        questionsLevel1.add(new Question("Banana", "https://imagizer.imageshack.com/img923/6608/KHEQOG.png", "boker_tov", 1, 5));
        questionsLevel1.add(new Question("Tsipor", "https://imagizer.imageshack.com/img924/9495/ZRq1zq.png", "boker_tov", 1, 6));
        questionsLevel1.add(new Question("Gezer", "https://imagizer.imageshack.com/img922/7832/1H5nbJ.png", "boker_tov", 1, 7));
        questionsLevel1.add(new Question("Arnav", "https://imagizer.imageshack.com/img924/7688/p0TUVG.png", "boker_tov", 1, 8));
        questionsLevel1.add(new Question("Buba", "https://imagizer.imageshack.com/img924/8034/pbtjYZ.png", "boker_tov", 1, 9));
        questionsLevel1.add(new Question("Tof", "https://imagizer.imageshack.com/img923/4078/nJcqrL.png", "boker_tov", 1, 10));
        questionsLevel1.add(new Question("Kelev", "https://imagizer.imageshack.com/img922/964/ba8OYg.png", "boker_tov", 1, 11));
        questionsLevel1.add(new Question("Bait", "https://imagizer.imageshack.com/img921/3083/jGYCs2.png", "boker_tov", 1, 12));

        mSizeOfLevel1 = questionsLevel1.size();

        //Level 2 questions
        questionsLevel2.add(new Question("Motsets", "https://imagizer.imageshack.com/img923/6172/7vmOw4.png", "boker_tov", 2, 0));
        questionsLevel2.add(new Question("Ets", "https://imagizer.imageshack.com/img922/2371/PGyfTj.png", "boker_tov", 2, 1));
        questionsLevel2.add(new Question("Tapuach", "https://imagizer.imageshack.com/img924/1850/Bx2dNE.png", "boker_tov", 2, 2));
        questionsLevel2.add(new Question("Parpar", "https://imagizer.imageshack.com/img923/657/qVfkgx.png", "boker_tov", 2, 3));
        questionsLevel2.add(new Question("Leitsan", "https://imagizer.imageshack.com/img921/1976/q5pAcM.png", "boker_tov", 2, 4));
        questionsLevel2.add(new Question("Simla", "https://imagizer.imageshack.com/img921/1479/Uv1Rzi.png", "boker_tov", 2, 5));
        questionsLevel2.add(new Question("Barvaz", "https://imagizer.imageshack.com/img924/1157/wwEirS.png", "boker_tov", 2, 6));
        questionsLevel2.add(new Question("Gitara", "https://imagizer.imageshack.com/img923/4660/HGPhZO.png", "boker_tov", 2, 7));


        mSizeOfLevel2 = questionsLevel2.size();

        questionsData.add(questionsLevel1);
        questionsData.add(questionsLevel2);
    }

    public int getSizeOfLevel(int level){
        int size;
        switch(level){
            case 1:
                size = this.mSizeOfLevel1;
                break;
            case 2:
                size = this.mSizeOfLevel2;
                break;
            default:
                size = this.mSizeOfLevel1;
        }
        return size;
    }

    public Question getRandomQuestion(int level){
        Question randQuestion = null;
        int index;
        int max;
        max = getSizeOfLevel(level);
        index = (int)(Math.random() * (max));
        System.out.println("~~~~~~~~~~~~~~~level: "+ level + "index: "+ index);
        randQuestion = questionsData.get(level-1).get(index);
        return randQuestion;
    }
}
