package com.mind.mind_calc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mind.mind_calc.Adapters.PlayerAdapter;
import com.mind.mind_calc.Constructors.GameRounds;
import com.mind.mind_calc.Constructors.Players;
import com.mind.mind_calc.Constructors.PlayersName;
import com.mind.mind_calc.Question.LevelEasy;
import com.mind.mind_calc.Question.LevelHard;
import com.mind.mind_calc.Question.LevelMedium;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Dialog exitDialog, loadingDialog, leaderBoardDialog,
            gamePlayDialog, timeOutDialog, correctAnswerDialog, wrongAnswerDialog;

    private TextView playerScore, playerName;
    private Button btnEasy, btnMedium, btnHard;
    private ImageButton btnScoreBoard, btnLogout;

    private String name="", level="", userId="", won="", loss="", rounds="",
            mode = "", question="", displayQuestionValue="";

    private CountDownTimer countDownTimer;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        Get layouts
        exitDialog = new Dialog(DashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        loadingDialog = new Dialog(DashboardActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog_box);

        leaderBoardDialog = new Dialog(DashboardActivity.this);
        leaderBoardDialog.setContentView(R.layout.leader_board_dialog_box);

        gamePlayDialog = new Dialog(DashboardActivity.this);
        gamePlayDialog.setContentView(R.layout.game_dialog_box);

        timeOutDialog = new Dialog(DashboardActivity.this);
        timeOutDialog.setContentView(R.layout.time_out_dialog_box);

        correctAnswerDialog = new Dialog(DashboardActivity.this);
        correctAnswerDialog.setContentView(R.layout.answer_correct_dialog_box);

        wrongAnswerDialog = new Dialog(DashboardActivity.this);
        wrongAnswerDialog.setContentView(R.layout.answer_wrong_dialog_box);

        playerScore = (TextView) this.findViewById(R.id.playerScore);
        playerName = (TextView) this.findViewById(R.id.playerName);

        btnEasy = (Button) this.findViewById(R.id.btnEasy);
        btnMedium = (Button) this.findViewById(R.id.btnMedium);
        btnHard = (Button) this.findViewById(R.id.btnHard);

        btnScoreBoard = (ImageButton) this.findViewById(R.id.btnScoreBoard);
        btnLogout = (ImageButton) this.findViewById(R.id.btnLogout);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        ref = FirebaseDatabase.getInstance().getReference("players/"+userId);

//        Show player details
        showPlayerDetails();

//        Button events
        btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGame("LevelEasy");
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGame("LevelMedium");
            }
        });

        btnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGame("LevelHard");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaderBoard();
            }
        });

    }

//    Hide status bar and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);

        Button btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        Button btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);

        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for show player details
    private void showPlayerDetails()
    {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("name").exists()) {

                    name = snapshot.child("name").getValue().toString();
                    playerName.setText(name);

                    if (snapshot.child("game").exists()) {
                        level = snapshot.child("game").child("level").getValue().toString();
                        won = snapshot.child("game").child("won").getValue().toString();
                        loss = snapshot.child("game").child("loss").getValue().toString();
                        rounds = snapshot.child("game").child("rounds").getValue().toString();
                    } else {
                        won = "0";
                        loss = "0";
                        rounds = "0";
                        level = "0";
                    }

                    playerScore.setText(level);
                } else {
                    firebaseAuth.signOut();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                firebaseAuth.signOut();
                Toast.makeText(DashboardActivity.this, "Some error occur! Try again", Toast.LENGTH_SHORT).show();

            }
        });

    }

//    Method for show game play dialog box
    private void showGame(String mode)
    {
        gamePlayDialog.show();
        gamePlayDialog.setCancelable(false);
        gamePlayDialog.setCanceledOnTouchOutside(false);

        Button btnClose = (Button) gamePlayDialog.findViewById(R.id.btnClose);
        Button btnSubmitAnswer = (Button) gamePlayDialog.findViewById(R.id.btnSubmitAnswer);

        TextView textQuestion = (TextView) gamePlayDialog.findViewById(R.id.textQuestion);
        TextView textTime = (TextView) gamePlayDialog.findViewById(R.id.textTime);
        TextView gameTitle = (TextView) gamePlayDialog.findViewById(R.id.gameTitle);
        EditText textAnswer = (EditText) gamePlayDialog.findViewById(R.id.textAnswer);

        String modeName = "";

        if (mode.equals("LevelEasy")){
            modeName = "Easy";
        } else if (mode.equals("LevelMedium")){
            modeName = "Medium";
        } else if (mode.equals("LevelHard")){
            modeName = "Hard";
        }

        gameTitle.setText(modeName+" Round");
        questionCreator(mode, textAnswer, textQuestion, textTime, btnSubmitAnswer);

        btnSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerValidator(textAnswer, textTime, textQuestion, mode, btnSubmitAnswer);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });

    }

//    Method for open dashboard activity
    private void showExitDialog()
    {
        exitDialog.show();
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);

        Button btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        Button btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        TextView textView = (TextView) exitDialog.findViewById(R.id.textView);

        textView.setText("Do you want exit game?");

        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                exitDialog.dismiss();
                gamePlayDialog.dismiss();
            }
        });

        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for create questions
    private String questionCreator(String mode, EditText textAnswer,
                                   TextView textQuestion, TextView textTime, Button btnSubmitAnswer)
    {
        textAnswer.setEnabled(true);
        btnSubmitAnswer.setEnabled(true);
        textAnswer.setFocusable(true);

        textAnswer.setText("");

        String value = "";
        String displayValue = "";

        if (mode.equals("LevelEasy")) {

            LevelEasy levelEasy = new LevelEasy();
            value = levelEasy.generateQuestion();
            countDownTimer(25000, textTime, textAnswer, textQuestion, mode, btnSubmitAnswer);

        } else if (mode.equals("LevelMedium")) {

            LevelMedium levelMedium = new LevelMedium();
            value = levelMedium.generateQuestion();
            countDownTimer(20000, textTime, textAnswer, textQuestion, mode, btnSubmitAnswer);

        } else if (mode.equals("LevelHard")) {

            LevelHard levelHard = new LevelHard();
            value = levelHard.generateQuestion();
            countDownTimer(15000, textTime, textAnswer, textQuestion, mode, btnSubmitAnswer);

        }

        question = value;

//        Replace unicode characters
        displayValue = value.replace("/","\u00F7");
        displayValue = displayValue.replace("*","\u00D7");

        displayQuestionValue = displayValue;

        textQuestion.setText(displayValue+" = ?");
        return value;
    }

//    Method for count down
    private void countDownTimer(int maxTime, TextView textTime,
                                EditText textAnswer, TextView textQuestion, String mode, Button btnSubmitAnswer)
    {
        countDownTimer = new CountDownTimer(maxTime+1000, 1000) {

            public void onTick(long millisUntilFinished) {

                if ((millisUntilFinished / 1000) < 6) {
                    textTime.setTextColor(getResources().getColor(R.color.red1));
                } else {
                    textTime.setTextColor(getResources().getColor(R.color.gray1));
                }

                if ((millisUntilFinished / 1000) < 10) {
                    textTime.setText("00:0" + millisUntilFinished / 1000);
                } else {
                    textTime.setText("00:" + millisUntilFinished / 1000);
                }

            }

            public void onFinish() {
                textAnswer.setText("");
                textTime.setText("Time Out!");

                textAnswer.setText("");

                showTimeOutDialogBox(textTime, textAnswer, textQuestion, mode, btnSubmitAnswer);

            }

        }.start();
    }

//    Method for show show time out dialog box
    private void showTimeOutDialogBox(TextView textTime, EditText textAnswer,
                                      TextView textQuestion, String mode, Button btnSubmitAnswer)
    {
        addScore(mode,"wrong");

        timeOutDialog.show();
        timeOutDialog.setCancelable(false);
        timeOutDialog.setCanceledOnTouchOutside(false);

        Button btnExit = (Button) timeOutDialog.findViewById(R.id.btnExit);
        Button btnTryAgain = (Button) timeOutDialog.findViewById(R.id.btnTryAgain);
        TextView textViewQuestion = (TextView) timeOutDialog.findViewById(R.id.textQuestion);

        String replacedQuestion = replaceCharacters(question);

//            Get html response
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String getUrl = "https://api.mathjs.org/v4/?expr="+replacedQuestion;

        StringRequest getRequest = new StringRequest(Request.Method.GET, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse (String response) {
                textViewQuestion.setText(displayQuestionValue+" = "+ response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                loadingDialog.dismiss();
                System.out.println(error.getMessage());
            }
        });

        loadingDialog.dismiss();
        requestQueue.add(getRequest);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeOutDialog.dismiss();
                gamePlayDialog.dismiss();
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeOutDialog.dismiss();
                questionCreator(mode, textAnswer, textQuestion, textTime, btnSubmitAnswer);
            }
        });
    }

//    Method for sign out user
    private void signOut()
    {
        exitDialog.show();
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);

        Button btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        Button btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        TextView textView = (TextView) exitDialog.findViewById(R.id.textView);

        textView.setText("Do you want sign out?");

        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            }
        });

        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for show leader board
    private void showLeaderBoard()
    {
        leaderBoardDialog.show();
        leaderBoardDialog.setCancelable(false);
        leaderBoardDialog.setCanceledOnTouchOutside(false);

        Button btnClose = (Button) leaderBoardDialog.findViewById(R.id.btnClose);
        ListView leaderList = (ListView) leaderBoardDialog.findViewById(R.id.leaderList);
        ArrayList<PlayersName> arrayList = new ArrayList<>();
        leaderList.setAdapter(null);

        PlayerAdapter playerAdapter = new PlayerAdapter(this, R.layout.view_players_row, arrayList);
        leaderList.setAdapter(playerAdapter);

        List<Players> sortList = new ArrayList<Players>();

        ref = FirebaseDatabase.getInstance().getReference("players/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    if (snapshot1.child("name").exists()) {

                        String name = snapshot1.child("name").getValue().toString();
                        String level = "0";
                        if (snapshot1.child("game").exists()) {
                            level = snapshot1.child("game").child("level").getValue().toString();
                        }

                        int playerLevel = Integer.parseInt(level);
                        sortList.add(new Players(playerLevel, name));

                    }

                }

//                Sort list
                Collections.sort(sortList);
                Collections.reverse(sortList);

                int count = 1;
                for (Players players : sortList) {
                    if (count < 11) {
                        arrayList.add(new PlayersName(String.valueOf(count)+". "+players.toString()));
                        playerAdapter.notifyDataSetChanged();
                    }

                    count += 1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaderBoardDialog.dismiss();
            }
        });

    }

//    Method for replace values
    private String replaceCharacters(String value)
    {
        String replaced = "";
        replaced = value.replace("/","%2F");
        replaced = replaced.replace("+","%2B");

        return replaced;
    }

//    Method for get http response and validate answer
    private void answerValidator(EditText textAnswer, TextView textTime,
                                 TextView textQuestion, String mode, Button btnSubmitAnswer)
    {
        String answer = textAnswer.getText().toString();

        if (answer.isEmpty()) {
            Toast.makeText(DashboardActivity.this,"Please enter your answer!", Toast.LENGTH_SHORT).show();
        } else {

            textAnswer.setEnabled(false);
            btnSubmitAnswer.setEnabled(false);

            Toast.makeText(DashboardActivity.this,"Waiting for validate answer!", Toast.LENGTH_SHORT).show();

            loadingDialog.show();
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);

            TextView textMsg = (TextView) loadingDialog.findViewById(R.id.textMsg);
            textMsg.setText("Answer Validating...");

            countDownTimer.cancel();
            textTime.setTextColor(getResources().getColor(R.color.gray1));
            textTime.setText("00:00");

            answer = answer.replace(" ","");

            String replacedQuestion = replaceCharacters(question);

//            Get html response
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String getUrl = "https://api.mathjs.org/v4/?expr="+replacedQuestion;

            String finalAnswer = answer;
            StringRequest getRequest = new StringRequest(Request.Method.GET, getUrl, new Response.Listener<String>() {
                @Override
                public void onResponse (String response) {

                    if (finalAnswer.equals(response.toString())) {
                        addScore(mode,"correct");
                        loadingDialog.dismiss();
                        showAnswerCorrectDialog(textAnswer, textQuestion, textTime, mode, btnSubmitAnswer);

                    } else {
                        addScore(mode,"wrong");
                        loadingDialog.dismiss();
                        showAnswerWrongDialog(displayQuestionValue+" = "+ response.toString(),
                                textAnswer, textQuestion, textTime, mode, btnSubmitAnswer);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse (VolleyError error) {
                    loadingDialog.dismiss();
                    System.out.println(error.getMessage());
                }
            });

            loadingDialog.dismiss();
            requestQueue.add(getRequest);

        }
    }

//    Method for answer correct dialog box
    private void showAnswerCorrectDialog(EditText textAnswer,
                                         TextView textQuestion, TextView textTime, String mode, Button btnSubmitAnswer)
    {

        correctAnswerDialog.show();
        correctAnswerDialog.setCancelable(false);
        correctAnswerDialog.setCanceledOnTouchOutside(false);

        Button btnExit = (Button) correctAnswerDialog.findViewById(R.id.btnExit);
        Button btnNextRound = (Button) correctAnswerDialog.findViewById(R.id.btnNextRound);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctAnswerDialog.dismiss();
                gamePlayDialog.dismiss();
            }
        });

        btnNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctAnswerDialog.dismiss();
                questionCreator(mode, textAnswer, textQuestion, textTime, btnSubmitAnswer);
            }
        });
    }

//    Method for answer wrong dialog box
    private void showAnswerWrongDialog(String value, EditText textAnswer,
                                       TextView textQuestion, TextView textTime, String mode, Button btnSubmitAnswer)
    {
        wrongAnswerDialog.show();
        wrongAnswerDialog.setCancelable(false);
        wrongAnswerDialog.setCanceledOnTouchOutside(false);

        TextView textQuestionWrongAnswer = (TextView) wrongAnswerDialog.findViewById(R.id.textQuestion);
        Button btnExit = (Button) wrongAnswerDialog.findViewById(R.id.btnExit);
        Button btnTryAgain = (Button) wrongAnswerDialog.findViewById(R.id.btnTryAgain);

        textQuestionWrongAnswer.setText(value);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongAnswerDialog.dismiss();
                gamePlayDialog.dismiss();
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongAnswerDialog.dismiss();
                questionCreator(mode, textAnswer, textQuestion, textTime, btnSubmitAnswer);
            }
        });
    }

//    Method for update firebase
    private void addScore(String mode, String status)
    {
        int wonCount = Integer.parseInt(won);
        int lossCount = Integer.parseInt(loss);
        int roundsCount = Integer.parseInt(rounds);
        int levelCount = Integer.parseInt(level);

        roundsCount = roundsCount + 1;

        if (status.equals("correct")) {
            wonCount = wonCount + 1;
        } else {
            lossCount = lossCount + 1;
        }

        if (mode.equals("LevelEasy")) {

            if (status.equals("correct")) {
                levelCount = levelCount + 1;
            } else {
                levelCount = levelCount - 1;
            }

        } else if (mode.equals("LevelMedium")) {

            if (status.equals("correct")) {
                levelCount = levelCount + 2;
            } else {
                levelCount = levelCount - 2;
            }

        } else if (mode.equals("LevelHard")) {

            if (status.equals("correct")) {
                levelCount = levelCount + 3;
            } else {
                levelCount = levelCount - 3;
            }

        }

        ref = FirebaseDatabase.getInstance().getReference("players/"+userId);
        GameRounds gameRounds = new GameRounds(wonCount, lossCount, roundsCount, levelCount);

        int finalWonCount = wonCount;
        int finalLossCount = lossCount;
        int finalRoundsCount = roundsCount;
        int finalLevelCount = levelCount;

        ref.child("game").setValue(gameRounds).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    won = String.valueOf(finalWonCount);
                    loss = String.valueOf(finalLossCount);
                    rounds = String.valueOf(finalRoundsCount);
                    level = String.valueOf(finalLevelCount);

                    playerScore.setText(level);
                }
            }
        });

    }

}