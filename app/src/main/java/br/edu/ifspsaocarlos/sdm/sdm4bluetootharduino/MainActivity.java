package br.edu.ifspsaocarlos.sdm.sdm4bluetootharduino;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.StringCharacterIterator;

public class MainActivity extends Activity {

    /* Definição dos objetos que serão usados na Activity Principal
        statusMessage mostrará mensagens de status sobre a conexão
        counterMessage mostrará o valor do contador como recebido do Arduino
        connect é a thread de gerenciamento da conexão Bluetooth
     */
    private static  TextView statusMessage;
    private static  TextView counterMessage;
    private static TextView statusSensor;
    private static Button btReiniciar;
    private static String LEITURADOSENSOR = "";
    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Link entre os elementos da interface gráfica e suas
            representações em Java.
         */
        statusMessage = (TextView) findViewById(R.id.statusMessage);
        counterMessage = (TextView) findViewById(R.id.counterMessage);
        statusSensor = (TextView) findViewById(R.id.statusSensor);
        btReiniciar = (Button) findViewById(R.id.btReiniciar);

        /* Teste rápido. O hardware Bluetooth do dispositivo Android
            está funcionando ou está bugado de forma misteriosa?
            Será que existe, pelo menos? Provavelmente existe.
         */
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText("Que pena! Hardware Bluetooth não está funcionando :(");
        } else {
            statusMessage.setText("Ótimo! Hardware Bluetooth está funcionando :)");
        }


        btReiniciar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                connect.write("sensorOn\n".getBytes());
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });

        /* A chamada do seguinte método liga o Bluetooth no dispositivo Android
            sem pedido de autorização do usuário. É altamente não recomendado no
            Android Developers, mas, para simplificar este app, que é um demo,
            faremos isso. Na prática, em um app que vai ser usado por outras
            pessoas, não faça isso.
         */
        btAdapter.enable();

        /* Definição da thread de conexão como cliente.
            Aqui, você deve incluir o endereço MAC do seu módulo Bluetooth.
            O app iniciará e vai automaticamente buscar por esse endereço.
            Caso não encontre, dirá que houve um erro de conexão.
         */
        connect = new ConnectionThread("00:21:13:03:5B:0D");  // insira o MAC do seu dispositivo aqui
        connect.start();

        /* Um descanso rápido, para evitar bugs esquisitos.
         */
        try {
            Thread.sleep(1000);
        } catch (Exception E) {
            E.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            /* Esse método é invocado na Activity principal
                sempre que a thread de conexão Bluetooth recebe
                uma mensagem.
             */
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            /* Aqui ocorre a decisão de ação, baseada na string
                recebida. Caso a string corresponda à uma das
                mensagens de status de conexão (iniciadas com --),
                atualizamos o status da conexão conforme o código.
             */
            if(dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                statusMessage.setText("Conectado :D");
            else{
                if (dataString.equals("Sensor Desligado"))
                    statusMessage.setText("Conectado e sensor de luz off :D");
                    else{

                    /* Se a mensagem não for um código de status,
                    então ela deve ser tratada pelo aplicativo
                    como uma mensagem vinda diretamente do outro
                    lado da conexão. Nesse caso, simplesmente
                    atualizamos o valor contido no TextView do
                    contador.

                 */
                    if (dataString.length()>0) {
                        String dataStringX = dataString.substring(0,1);
                        String bufferOriginal = dataString;

                        if (dataStringX.equals("L")) {
                            if (bufferOriginal.length()>=4) {
                                statusSensor.setText("LDR: "+bufferOriginal.substring(3));
                            }else{
                                statusSensor.setText("LDR: "+bufferOriginal);
                            }
                            //counterMessage.setText(dataString);

                        } else {
                            counterMessage.setText("CONT:"+bufferOriginal);
                            //statusSensor.setText(dataString);
                        }
                    }

                }
            }
            }

    };

    /* Esse método é invocado sempre que o usuário clicar na TextView
        que contem o contador. O app Android transmite a string "restart",
        seguido de uma quebra de linha, que é o indicador de fim de mensagem.
     */
    public void restartCounter(View view) {
        connect.write("restart\n".getBytes());
    }

}
