package com.example.celebrityguess;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String html;
    downloadweb task;
    downloadImage Imagetask;
    ImageView imageView;
    URL url;
    ArrayList<String> imageIndex = new ArrayList<String>();
    ArrayList<String> nameIndex = new ArrayList<String>();
    String btnAnswers[] = new String[4];
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    int buttons;
    int previousImageLocation;
    int correctAnswerLocation;
    Random random;

    public class downloadweb extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {

                url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream IS = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(IS);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }
    }

    public class downloadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String result = "";
            try {

                url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream IS = httpURLConnection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(IS);
                return mybitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public void celebdata() {
        try {
            html = task.execute("https://stylesatlife.com/articles/famous-female-celebrities-in-bollywood/").get();
            String[] splitString = html.split("Hot Bollywood Actress Pics:");
            Pattern PforImage = Pattern.compile("<amp-img src=(.*?) alt=\"");
            Matcher MforImage = PforImage.matcher(splitString[1]);
            for (int i = 0; i <= 29; i++) {
                MforImage.find();
                imageIndex.add(MforImage.group(1));
            }

            Pattern Pforname = Pattern.compile("</p><h4>(.*?)</h4><p>");
            Matcher Mforname = Pforname.matcher(splitString[1]);
            while (Mforname.find()) {
                Pattern Pedit = Pattern.compile(". (.*?):");
                Matcher Medit = Pedit.matcher(Mforname.group(1));
                while (Medit.find()) {
                    if (nameIndex.size() == 21) {
                        nameIndex.add(21, "Preity Zinta");
                    }
                    nameIndex.add(Medit.group(1));
                    if (nameIndex.size() == 30) {
                        break;
                    }
                }
                if (nameIndex.size() == 30) {
                    break;
                }
            }
            html=null;
            splitString[0]=null;
            splitString[1]=null;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void celebchanger() {

        random = new Random();
        correctAnswerLocation = random.nextInt(30);
        while(previousImageLocation==correctAnswerLocation){
            correctAnswerLocation = random.nextInt(30);
        }
        Bitmap imagechanger = null;
        Imagetask = new downloadImage();
        try {
            imagechanger = Imagetask.execute(imageIndex.get(correctAnswerLocation)).get();
            previousImageLocation=correctAnswerLocation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(imagechanger);

        buttons = random.nextInt(4);
        for (int i = 0; i <= 3; i++) {
            if (i == buttons) {
                btnAnswers[i] = nameIndex.get(correctAnswerLocation);
            } else {
                int differentanswer = random.nextInt(30);
                while (differentanswer == correctAnswerLocation) {
                    differentanswer = random.nextInt(30);
                }
                btnAnswers[i] = nameIndex.get(differentanswer);
            }
        }
        button1.setText(btnAnswers[0]);
        button2.setText(btnAnswers[1]);
        button3.setText(btnAnswers[2]);
        button4.setText(btnAnswers[3]);

    }

    public void onClick(View V) {
        if (V.getTag().toString().equals(Integer.toString(buttons))) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            celebchanger();
        } else {
            Toast.makeText(this, "wrong!", Toast.LENGTH_SHORT).show();
            celebchanger();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        html = null;
        task = new downloadweb();
        celebdata();
        celebchanger();
    }
}