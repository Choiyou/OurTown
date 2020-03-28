package com.example.os150.ourtown;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

/**
 * Created by os150 on 2020-03-20.
 */

public class SigninActivity extends ActivityGroup{
    private TextView resetPassword;
    private TextView Signup;
    private Button Signinbtn;
    private EditText emailEdit;
    private EditText passwordEdit;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        resetPassword = findViewById(R.id.resetpasswordtext);
        Signup = findViewById(R.id.signuptext);
        Signinbtn = findViewById(R.id.Signinbtn);
        emailEdit = findViewById(R.id.siemailedtext);
        passwordEdit = findViewById(R.id.sipasswordedtext);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();


            //로그인 버튼 클릭
            Signinbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String email = emailEdit.getText().toString();
                    String password = passwordEdit.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        emailEdit.setError("이메일을 입력해주세요");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        passwordEdit.setError("비밀번호를 입력해주세요");
                        return;
                    }
                    //이메일&비밀번호 로그인 함수
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent tabintent = new Intent(SigninActivity.this, MembershipActivity.class);
                                Window window = getLocalActivityManager().startActivity("test", tabintent);
                                setContentView(window.getDecorView());
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인 실패!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            });

            // 비밀번호 재설정 ( 이메일 전송 )
            resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final EditText sendemail = new EditText(SigninActivity.this);
                        sendemail.setSingleLine();
                        sendemail.getTransformationMethod();
                        AlertDialog.Builder alert_sendemail = new AlertDialog.Builder(SigninActivity.this);
                        alert_sendemail.setTitle("암호 재설정");
                        alert_sendemail.setView(sendemail);

                        alert_sendemail.setMessage("등록한 이메일을 입력해주세요").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (!TextUtils.isEmpty(sendemail.getText().toString())) {
                                            mAuth.sendPasswordResetEmail(sendemail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "이메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                            });

                                        } else {
                                            Toast.makeText(getApplicationContext(), "입력칸이 비어있습니다.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                        );
                        alert_sendemail.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(SigninActivity.this, "취소", Toast.LENGTH_LONG).show();
                            }
                        });
                        alert_sendemail.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //회원가입 textView 클릭 시 회원가입 화면으로 전환
            Signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                }
            });


        }
    }

