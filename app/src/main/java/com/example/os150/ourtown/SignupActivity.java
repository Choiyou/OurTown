package com.example.os150.ourtown;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by os150 on 2020-03-20.
 */

//이메일 / 비밀번호 / 전화번호 유효성 검사 체크하기
public class SignupActivity extends Activity {
    private FirebaseAuth mAuth;
    private EditText mName;
    private EditText mPhonenum;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordcheck;
    private Button signupbtn;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signupbtn = findViewById(R.id.Signupbtn);
        mName = findViewById(R.id.nameedtext);
        mPhonenum = findViewById(R.id.phonenumedtext);
        mEmail = findViewById(R.id.emailedtext);
        mPassword = findViewById(R.id.passwordedtext);
        mPasswordcheck = findViewById(R.id.passwordcheckedtext);

        //로그인 상태일 경우 개인정보 화면으로 전환
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MembershipActivity.class));
        }

        signupbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String name = mName.getText().toString();
                final String phonenum = mPhonenum.getText().toString();
                final String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String passwordcheck = mPasswordcheck.getText().toString();
                final String profile = getResources().getDrawable(R.drawable.drawable_userimage).toString();

                if (TextUtils.isEmpty(name)) {
                    mName.setError("이름을 입력해주세요");
                    return;
                } else if (TextUtils.isEmpty(phonenum)) {
                    mPhonenum.setError("전화번호를 입력해주세요");
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    mEmail.setError("이메일을 입력해주세요");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    mPassword.setError("비밀번호를 입력해주세요");
                    return;
                } else if (TextUtils.isEmpty(passwordcheck)) {
                    mPasswordcheck.setError("비밀번호 확인을 입력해주세요");
                    return;
                }
                if (password.length() <= 5) {
                    mPassword.setError("6글자 이상 입력해주세요");
                    mPassword.setText("");
                }
                if (!passwordcheck.equals(password)) {
                    mPasswordcheck.setError("비밀번호가 일치하지 않습니다");
                    mPasswordcheck.setText("");
                    mPasswordcheck.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("이메일 형식이 아닙니다");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                 //   final String uid = task.getResult().getUser().getUid();

                                    mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                    UserData userData = new UserData(name, profile, email, phonenum);
                                    mDatabase.child("usersInfo").child(mAuth.getCurrentUser().getUid()).setValue(userData);


                                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MembershipActivity.class));

                                } else {

                                    Toast.makeText(SignupActivity.this, "회원가입 실패.", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
            }
        });
    }
}