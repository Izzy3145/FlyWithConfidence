package uk.airbyte.fwc.fragments.auth;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Reminder;
import uk.airbyte.fwc.viewmodels.AuthViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotFragment extends Fragment {

    private static final String TAG = ForgotFragment.class.getSimpleName();

    @BindView(R.id.btnSignIn)
    Button signInBtn;
    @BindView(R.id.backSignIn)
    Button backSignInBtn;
    @BindView(R.id.inputEmailAddress)
    TextInputEditText inputEmailAddress;
    @BindView(R.id.inputLayoutEmailAddress)
    TextInputLayout inputLayoutEmailAddress;
    @BindView(R.id.sendGroup)
    Group sendGroup;
    @BindView(R.id.sentGroup)
    Group sentGroup;
    @BindView(R.id.sentEmailTv)
    TextView sentEmailTv;
    @BindView(R.id.loading_plane)
    ImageView loadingPlane;
    private String email;
    private AuthViewModel mAuthViewModel;

    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);
        ButterKnife.bind(this, view);
        backSignInBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_forgotFragment_to_signInFragment));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuthViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
    }

    @OnClick(R.id.btnSignIn)
    public void signIn() {
        signInBtn.setVisibility(View.GONE);
        loadingPlane.setVisibility(View.VISIBLE);
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        loadingPlane.startAnimation(rotate);

        if (!validateEmail()) {
            return;
        }

        email = inputEmailAddress.getText().toString().trim();
        Log.d(TAG, "Email address: " + email);

        mAuthViewModel.getForgottenPw(getActivity(), email).observe(this, new Observer<Reminder>() {
            @Override
            public void onChanged(@Nullable Reminder reminder) {
                if (reminder != null) {
                    if (reminder.getSent()) {

                        //TODO: make email address bold
                        SpannableStringBuilder boldEmail = new SpannableStringBuilder(email);
                        boldEmail.setSpan(new StyleSpan(Typeface.BOLD), 0, email.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        sentEmailTv.setText(String.format(getResources().getString(R.string.sent_reminder_email),
                                boldEmail));
                        sendGroup.setVisibility(View.GONE);
                        sentGroup.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Email address not registered", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "Reminder received: " + reminder.toString());
                }
            }
        });
    }

    private Boolean validateEmail() {
        String email = inputEmailAddress.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmailAddress.setError("Enter a valid email address");
            inputEmailAddress.requestFocus();
            return false;
        } else {
            inputLayoutEmailAddress.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuthViewModel.closeRealm();
    }
}
