package top.navyblue.leandemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private Context mContext;

    @InjectView(R.id.input_phoneNumber) EditText mEtPhoneNumber;
    @InjectView(R.id.input_checkCode) EditText mEtCheckCode;
    @InjectView(R.id.input_email) EditText mEtEmail;
    @InjectView(R.id.btn_send) Button mBtnSend;
    @InjectView(R.id.btn_check) Button mBtnCheck;
    @InjectView(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        mContext = this;

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mEtPhoneNumber.getText().toString();
                sendCode(phoneNumber);
            }
        });

        mBtnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mEtPhoneNumber.getText().toString();
                String checkCode = mEtCheckCode.getText().toString();
                verifyAndRegister(checkCode, phoneNumber);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void sendCode(final String phone) {
        new AsyncTask<Void, Void, Void>() {
            boolean res;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    AVOSCloud.requestSMSCode(phone, "SmsDemo", "注册", 10);
                    res = true;
                } catch (AVException e) {
                    e.printStackTrace();
                    res = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (res) {
                    toast(R.string.sendSucceed);
                } else {
                    toast(R.string.sendFailed);
                }
            }
        }.execute();
    }

    private void verifyAndRegister(String code, final String phoneNumber) {
        AVOSCloud.verifySMSCodeInBackground(code, phoneNumber,
                new AVMobilePhoneVerifyCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            toast(R.string.verifySucceed);

                            AVUser user = new AVUser();
                            user.setUsername(phoneNumber);
                            user.setPassword(phoneNumber);
                            user.setEmail(mEtEmail.getText().toString());
                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        toast("注册成功");
                                    } else {
                                        switch (e.getCode()) {
                                            case 202:
                                                toast("手机已注册");
                                                break;
                                            case 203:
                                                toast("邮箱已注册");
                                                break;
                                            default:
                                                toast("网络错误");
                                                break;
                                        }
                                    }
                                }
                            });

                        } else {
                            e.printStackTrace();
                            toast(R.string.verifyFailed);
                        }
                    }
                });
    }

    private void toast(int id) {
        Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
    }
    private void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

}