package sodevan.sarcar;

import android.content.Context;
import android.speech.tts.TextToSpeech;

/**
 * Created by kartiksharma on 22/01/17.
 */

public class Speaker implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean ready=false;
    private boolean allowed=false;
    public Speaker(Context context){
        tts=new TextToSpeech(context,this);
    }

    @Override
    public void onInit(int i) {

    }
}
