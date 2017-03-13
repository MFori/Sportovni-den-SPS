package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cz.sps_pi.sportovni_den.R;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class LoginFragment3 extends LoginFragment implements View.OnClickListener {

    private EditText name, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment3, container, false);

        name = (EditText) view.findViewById(R.id.login_referee_name);
        password = (EditText) view.findViewById(R.id.login_referee_pass);
        Button submit = (Button) view.findViewById(R.id.login_referee_submit);
        submit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        String name = this.name.getText().toString();
        String password = this.password.getText().toString();

        if (name.isEmpty())
            Toast.makeText(getContext(), "Zadejte jméno!", Toast.LENGTH_SHORT).show();
        else if (password.isEmpty())
            Toast.makeText(getContext(), "Zadejte heslo!", Toast.LENGTH_SHORT).show();
        else
            getCallback().loginReferee(name, password, this);
    }

    @Override
    public void onLoginError() {
        Toast.makeText(getContext(), "Nepodařilo se přihlásit", Toast.LENGTH_LONG).show();
        password.setText("");
    }
}
