package com.example.os150.ourtown;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.Set;

/**
 * Created by os150 on 2020-03-23.
 */

public class MembershipActivity extends ActivityGroup{
    private Button Logoutbtn;
    private Button Withdrawal;
    private Button Resetpw;
    private Button SetProfile;
    private FirebaseAuth mAuth;
    private TextView profile;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    DatabaseReference mDatabase ;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        profile = findViewById(R.id.memberInfo);
        Logoutbtn = findViewById(R.id.logoutbtn);
        Withdrawal = findViewById(R.id.withdrawal);
        Resetpw = findViewById(R.id.resetpassword);
        SetProfile = findViewById(R.id.setprofile);
        mAuth = FirebaseAuth.getInstance();


        final String userid = user.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child("usersInfo").child(userid).child("name");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                profile.setText("회원 : " + name);
                Log.v("알림 ", "이름 :" + name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("알림 : ", "데이터 값 읽어오는 것을 실패하였습니다.");
            }
        });



        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        Resetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final EditText resetpwed = new EditText(MembershipActivity.this);
                    resetpwed.getTransformationMethod();
                    AlertDialog.Builder alert_resetpw = new AlertDialog.Builder(getParent());
                    alert_resetpw.setTitle("비밀번호 변경");
                    alert_resetpw.setView(resetpwed);
                    alert_resetpw.setMessage("비밀번호 변경 이후 되돌릴 수 없습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    user.updatePassword(resetpwed.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MembershipActivity.this, "비밀번호가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    });
                                }
                            }
                    );
                    alert_resetpw.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MembershipActivity.this, "취소", Toast.LENGTH_LONG).show();
                        }
                    });
                    alert_resetpw.show();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    AlertDialog.Builder alert_delete = new AlertDialog.Builder(getParent());
                    alert_delete.setMessage("계정 삭제를 진행합니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(MembershipActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                }
                                            });
                                    mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                    mDatabase.child("users").child(user.getUid()).setValue(null);

                                }

                            }
                    );
                    alert_delete.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MembershipActivity.this, "취소", Toast.LENGTH_LONG).show();
                        }
                    });
                    alert_delete.show();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        SetProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
//    public void onBackPressed(){
//
//        long tempTime = System.currentTimeMillis();
//        long intervalTime = tempTime - backPressedTime;
//        if(0<=intervalTime&&FINISH_INTERVAL_TIME>=intervalTime){
//            super.onBackPressed();
//
//        }
//        else {
//            backPressedTime = tempTime;
//            Toast.makeText(getApplicationContext(),"한번더",Toast.LENGTH_SHORT).show();
//        }
//    }
}
