package jp.techacademy.rei.tanaka.qa_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;

    private Button mButton;

    private ChildEventListener mFavoriteListener = new ChildEventListener() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference favoriteRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid());
            mButton = findViewById(R.id.button1);
            mButton.setText("お気に入り済み");

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteRef.removeValue();
                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference favoriteRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid());
            mButton = findViewById(R.id.button1);
            mButton.setText("お気に入り");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("QuestionUid", mQuestion.getQuestionUid());
                    favoriteRef.push().setValue(data);
                }
            });
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {

                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);


        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        mButton = findViewById(R.id.button1);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoriteRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid());
        if (user == null) {
            mButton.setVisibility(View.INVISIBLE);
        }

        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                }
            }
        });

        mAnswerRef = dataBaseReference.child(Const.ContentsPAH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

        mButton = findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference favoriteRef = dataBaseReference.child(Const.FavoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid());
                Map<String, String> data = new HashMap<String, String>();
                data.put("QuestionUid", mQuestion.getQuestionUid());
                favoriteRef.push().setValue(data);
            }
        });

        favoriteRef.addChildEventListener(mFavoriteListener);
    }
}
