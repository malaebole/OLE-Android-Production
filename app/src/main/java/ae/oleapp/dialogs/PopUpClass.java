package ae.oleapp.dialogs;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import ae.oleapp.R;

public class PopUpClass {

    //PopupWindow display method

            TextView newUserText, oldUserText, forgetPassword;

    public void showPopupWindow(final View view, Boolean login, String giftDetails) {


        //Create a View object yourself through inflater
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow, null);

        newUserText = popupView.findViewById(R.id.new_user_text);
        oldUserText = popupView.findViewById(R.id.old_user_text);
        forgetPassword = popupView.findViewById(R.id.forget_password);


        if (login){
            newUserText.setText(R.string.new_user_txt);
            oldUserText.setVisibility(View.VISIBLE);
            forgetPassword.setVisibility(View.VISIBLE);

        }else{
            newUserText.setText(giftDetails);
            oldUserText.setVisibility(View.GONE);
            forgetPassword.setVisibility(View.GONE);
        }

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        //        TextView test2 = popupView.findViewById(R.id.titleText);
        //        test2.setText("Janu chutiyap na kro kam karo!!");
        //        Button buttonEdit = popupView.findViewById(R.id.messageButton);
        //        buttonEdit.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //
        //                //As an example, display the message
        //                popupWindow.dismiss();
        //                //Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();
        //
        //            }
        //        });



        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                //Close the window when clicked

                return true;
            }
        });
    }

}