package net.cloud95.android.lession.sensor01;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class Sensor01Activity extends Activity {

    private SeekBar sensor_value;
    private ProgressBar light_value;
    private ImageView flash_switch;
    
    private SensorManager manager;
    private Sensor lightSensor;
    private LightListener lightListener;
    
    private Camera camera;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor01);

        // 設定自動開啟或關閉閃光燈亮度用的SeekBar元件
        sensor_value = (SeekBar)findViewById(R.id.sensor_value);
        // 顯示目前環境亮度
        light_value = (ProgressBar)findViewById(R.id.light_value);
        flash_switch = (ImageView)findViewById(R.id.flash_switch);
 
        // 取得感應設備管理員物件
        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // 取得環境亮度感應設備
        lightSensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);//取得光源感測元件
        // 建立感應設備改變監聽物件
        lightListener = new LightListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 註冊感應設備改變監聽事件
        manager.registerListener(lightListener, lightSensor,SensorManager.SENSOR_DELAY_UI);//最後一個參數是時間
        // 啟用相機
        camera = Camera.open();//要先取得相機才能用散光燈
    }
    
    @Override
    protected void onPause() {
        // 移除感應設備改變監聽事件
    	manager.unregisterListener(lightListener);
        // 清除相機資源
    	if(camera != null){
    		camera.release();
    	}
        super.onPause();
    }
    
    // 感應設備資訊改變監聽類別
    private class LightListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int value) {
            
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 讀取環境亮度數值
        	float light = event.values[0];//取第一筆
            // 顯示目前環境亮度
        	light_value.setProgress((int)light);//設進度條
            // 建立設定相機設備用的Parameters物件
        	Parameters params = camera.getParameters();
            // 如果環境亮度低於SeekBar設定的數值
        	if(light<sensor_value.getProgress()){
        		// 加入開啟閃光燈的設定
        		params.setFlashMode(Parameters.FLASH_MODE_TORCH);
        		flash_switch.setImageResource(R.drawable.flash_on_icon);
        	}else{
        		// 加入關閉閃光燈的設定
        		params.setFlashMode(Parameters.FLASH_MODE_OFF);
        		flash_switch.setImageResource(R.drawable.flash_off_icon);
        	}   
                
            // 設定相機
        	camera.setParameters(params);
        }
        
    }
    
}
