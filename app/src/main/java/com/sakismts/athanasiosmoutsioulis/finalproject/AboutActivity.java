package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        TextView about = (TextView)findViewById(R.id.tv_about);

        about.setText(Html.fromHtml("<h1>\n" +
                "        About us\n" +
                "    </h1><h2>\n" +
                "       The\n" +
                "        School of Engineering and Digital Arts</h2><p>\n" +
                "        The School of Engineering and Digital Arts has an excellent reputation for both\n" +
                "        its teaching and research. Based in the Jennison Building on the Canterbury Campus,\n" +
                "        the School has 30 <a href=\"http://www.eda.kent.ac.uk/school/staff_directory.aspx\">academic\n" +
                "            staff </a>members, with both academic and industrial experience, as well as\n" +
                "        a number of specialist visiting lecturers. With strong industrial links, much of\n" +
                "        our research is supported by commercial organisations; there is also a range of\n" +
                "        industrially sponsored prizes for best students in each of our years. We are a forward-looking\n" +
                "        and <a href=\"international.aspx\">culturally diverse</a> School and our courses are\n" +
                "        regularly reviewed to ensure that they are always up-to-date.\n" +
                "    </p>"));
        //Enable the links inside the textview
       about.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
