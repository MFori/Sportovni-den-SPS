package cz.sps_pi.sportovni_den.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Match;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.view.TeamTextView;

public class OnlineMatchActivity extends SportDenActivity {

    public static final String EXTRA_MATCH = "match";

    private Match match;
    private int score1 = 0, score2 = 0;
    private int finalScore1 = 0, finalScore2 = 0;
    private Integer setChangePoints = null;
    private boolean serviceLeft = true;
    private boolean startServiceLeft = true;

    private TextView score1TV, score2TV;
    private TextView set1TV, set2TV;
    private TextView timeTV;
    private View service1, service2;
    private RelativeLayout endDialog, pauseDialog;

    private CountDownTimer timer;
    private boolean end = false;
    private boolean pause = false;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_match);

        if (getIntent().getExtras() != null) {
            match = (Match) getIntent().getExtras().getSerializable(EXTRA_MATCH);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(match.getSport().getName());
            }
        }

        score1TV = (TextView) findViewById(R.id.online_match_score1);
        score2TV = (TextView) findViewById(R.id.online_match_score2);
        set1TV = (TextView) findViewById(R.id.online_match_sets1);
        set2TV = (TextView) findViewById(R.id.online_match_sets2);
        timeTV = (TextView) findViewById(R.id.online_match_time);
        service1 = findViewById(R.id.online_match_service1);
        service2 = findViewById(R.id.online_match_service2);

        endDialog = (RelativeLayout) findViewById(R.id.online_match_end_dialog);
        pauseDialog = (RelativeLayout) findViewById(R.id.online_match_pause_dialog);

        initScoreChangingControls();
        initBottomControls();
        initMatchControls();
    }

    /**
     * Init match controls and show init dialog
     */
    private void initMatchControls() {
        Sport sport = match.getSport();
        set1TV.setVisibility(sport.getSets() != null ? View.VISIBLE : View.GONE);
        set2TV.setVisibility(sport.getSets() != null ? View.VISIBLE : View.GONE);
        timeTV.setVisibility(sport.getTime() != null ? View.VISIBLE : View.GONE);

        if (sport.getTime() != null) {
            time = match.getSport().getTime() * 60 * 1000;
            startCountDown();
        } else if (sport.getSetPoints() != null) {
            showInitDialog(true);
        }
    }

    /**
     * Start countDown for time sport type
     * Update time textView in onTick method (1s)
     * Show end dialog in onFinish method
     */
    private void startCountDown() {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                time = l;
                timeTV.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))
                        )
                );
            }

            @Override
            public void onFinish() {
                if (!pause) {
                    end = true;
                    showEndDialog();
                }
            }
        };
        timer.start();
    }

    /**
     * Update textViews with scores and service dots
     */
    private void updateScoreViews() {
        score1TV.setText(String.valueOf(score1));
        score2TV.setText(String.valueOf(score2));
        set1TV.setText(String.valueOf(finalScore1));
        set2TV.setText(String.valueOf(finalScore2));

        if (match.getSport().getSets() != null || match.getSport().getSetPoints() != null) {
            service1.setVisibility(serviceLeft ? View.VISIBLE : View.GONE);
            service2.setVisibility(!serviceLeft ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Init score controls (+,-) and set click listeners
     */
    private void initScoreChangingControls() {
        Button up1 = (Button) findViewById(R.id.online_match_up_1);
        Button down1 = (Button) findViewById(R.id.online_match_down_1);
        Button up2 = (Button) findViewById(R.id.online_match_up_2);
        Button down2 = (Button) findViewById(R.id.online_match_down_2);
        up1.setOnClickListener(ChangeScoreListener);
        down1.setOnClickListener(ChangeScoreListener);
        up2.setOnClickListener(ChangeScoreListener);
        down2.setOnClickListener(ChangeScoreListener);
    }

    /**
     * Init bottom controls (save/exit) and listeners
     */
    private void initBottomControls() {
        TeamTextView team1 = (TeamTextView) findViewById(R.id.online_match_team1);
        TeamTextView team2 = (TeamTextView) findViewById(R.id.online_match_team2);
        team1.setTeam(match.getTeam1());
        team2.setTeam(match.getTeam2());

        Button cancel = (Button) findViewById(R.id.online_match_cancel);
        Button save = (Button) findViewById(R.id.online_match_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match.setScore1(finalScore1);
                match.setScore2(finalScore2);
                Intent intent = new Intent();
                intent.putExtra("match", match);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * Change score controls (+,-) click listener
     */
    private View.OnClickListener ChangeScoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (end) return;

            switch (view.getId()) {
                case R.id.online_match_up_1:
                    score1++;
                    serviceLeft = setChangePoints == null || serviceLeft;
                    break;
                case R.id.online_match_down_1:
                    if (score1 != 0) score1--;
                    break;
                case R.id.online_match_up_2:
                    score2++;
                    serviceLeft = setChangePoints != null && serviceLeft;
                    break;
                case R.id.online_match_down_2:
                    if (score2 != 0) score2--;
                    break;
            }

            calculateScore();
            updateScoreViews();
            Integer sets = match.getSport().getSets();
            Integer setPoints = match.getSport().getSetPoints();

            if (sets != null && (finalScore1 == sets || finalScore2 == sets)) {
                end = true;
                showEndDialog();
            } else if (setPoints != null && (finalScore1 == setPoints || finalScore2 == setPoints)) {
                end = true;
                showEndDialog();
            }
        }
    };

    /**
     * Show end dialog
     */
    private void showEndDialog() {
        TeamTextView team1 = (TeamTextView) findViewById(R.id.online_match_team1_end);
        TeamTextView team2 = (TeamTextView) findViewById(R.id.online_match_team2_end);
        team1.setTeam(match.getTeam1());
        team2.setTeam(match.getTeam2());
        TextView score1 = (TextView) findViewById(R.id.online_match_end_score1);
        TextView score2 = (TextView) findViewById(R.id.online_match_end_score2);
        score1.setText(String.valueOf(finalScore1));
        score2.setText(String.valueOf(finalScore2));

        endDialog.animate().alpha(1f).start();
        endDialog.setVisibility(View.VISIBLE);
    }

    /**
     * Show/Hide pause dialog with animation
     *
     * @param show show or hide
     */
    private void showPauseDialog(final boolean show) {
        pauseDialog.setAlpha(show ? 0f : 1f);
        pauseDialog.animate().alpha(show ? 1f : 0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pauseDialog.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }).start();
        pauseDialog.setVisibility(View.VISIBLE);
    }

    /**
     * Show/Hide init dialog with animation
     *
     * @param show show or hide
     */
    private void showInitDialog(final boolean show) {
        final RelativeLayout dialog = (RelativeLayout) findViewById(R.id.online_match_init_dialog);
        if (show) {
            final Switch s1 = (Switch) findViewById(R.id.online_match_service_switch);
            TeamTextView t1 = (TeamTextView) findViewById(R.id.online_match_service_team1);
            t1.setTeam(match.getTeam1());
            TeamTextView t2 = (TeamTextView) findViewById(R.id.online_match_service_team2);
            t2.setTeam(match.getTeam2());
            t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s1.setChecked(false);
                }
            });
            t2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s1.setChecked(true);
                }
            });
        }
        dialog.setAlpha(show ? 0f : 1f);
        dialog.animate().alpha(show ? 1f : 0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dialog.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }).start();
        dialog.setVisibility(View.VISIBLE);
    }

    /**
     * Calculate score and service
     */
    private void calculateScore() {
        if (match.getSport().getSets() == null ||
                (match.getSport().getSetPoints() != null && match.getSport().getSets() == null)) {
            finalScore1 = score1;
            finalScore2 = score2;
        } else {
            int setPoints = match.getSport().getSetPoints();
            if ((score1 >= setPoints || score2 >= setPoints) &&
                    Math.abs(score1 - score2) >= 2) {
                finalScore1 += score1 > score2 ? 1 : 0;
                finalScore2 += score2 > score1 ? 1 : 0;
                score1 = 0;
                score2 = 0;

                startServiceLeft = !startServiceLeft;
                serviceLeft = startServiceLeft;
            } else if (setChangePoints != null) {
                int i = (score1 + score2) / setChangePoints;
                serviceLeft = (i % 2 == 0) == startServiceLeft;
            }
        }
    }

    /**
     * On resume button click
     *
     * @param view button
     */
    public void OnResumeClick(View view) {
        pause = false;
        showPauseDialog(false);
        if (match.getSport().getTime() != null)
            startCountDown();
    }

    /**
     * On pause button click
     *
     * @param view button
     */
    public void OnPauseClick(View view) {
        pause = true;
        if (timer != null) timer.cancel();
        showPauseDialog(true);
    }

    /**
     * On start button click
     *
     * @param view button
     */
    public void OnStartClick(View view) {
        Switch s1 = (Switch) findViewById(R.id.online_match_service_switch);
        startServiceLeft = serviceLeft = !s1.isChecked();
        RadioButton r1 = (RadioButton) findViewById(R.id.online_match_radio1);
        if (r1.isChecked()) {
            setChangePoints = null;
        } else {
            EditText editText = (EditText) findViewById(R.id.online_match_edit_points);
            setChangePoints = Integer.valueOf(editText.getText().toString());
        }

        service1.setVisibility(serviceLeft ? View.VISIBLE : View.GONE);
        service2.setVisibility(!serviceLeft ? View.VISIBLE : View.GONE);
        showInitDialog(false);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.nothing, R.anim.to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
