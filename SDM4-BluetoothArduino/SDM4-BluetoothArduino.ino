#include <SoftwareSerial.h>

/* Definição de um objeto SoftwareSerial.
 * Usaremos os pinos 8 e 9, como RX e TX, respectivamente.
 */
SoftwareSerial serial(10, 11);

String data = "";
int counter = 0;
int led = 13;
int sensor = 0;
String sensorStatus="off";
int valorSensor=0;


/* Nosso setup inclui a inicialização do objeto SoftwareSerial,
 *  com uma baud rate de 9600 bps. A definição do pino do led
 *  como saída e um delay de 2 segundos, só para garantir que
 *  o módulo HC-06 ou HC-05 iniciou direitinho.
 */
void setup() {
  serial.begin(9600);
  Serial.begin(9600);
  pinMode(led, OUTPUT);
  delay(2000);
}

/* Vamos pelo loop passo a passo.
 */

   /* No início de cada loop, verificamos se há algo no buffer
   *  da nossa serial. Se houver bytes disponíveis, significa 
   *  que o Android enviou algo, então faremos a leitura do 
   *  novo caractere e adicionamos ao final da string data.
   */

 
void loop() {
  while(serial.available() > 0) {
    data += char(serial.read());
  }

      /* Se o Arduino receber a string "restart" seguida de uma
   *  quebra de linha, reinicializamos o contador e ligamos
   *  o led por um segundo. Esse comando indicará que a 
   *  comunicação no sentido Android -> Arduino está sendo 
   *  realizada corretamente.
   */
  valorSensor=analogRead(sensor);
  if(data == "restart\n") {
    digitalWrite(led, HIGH);
    sensorStatus="off\n";
    counter = 0;
    delay(1000);
    digitalWrite(led, LOW);
    Serial.println("CONTAGEM REINICIADA");
  }  
  if(data =="sensorOn\n"){
    sensorStatus="on\n";
    Serial.println("SENSOR ESTÁ EM :"+sensorStatus);
    digitalWrite(led, HIGH);
    delay(1000);
    digitalWrite(led, LOW);
  }
  if(data == "sensorOff\n"){
    sensorStatus="off\n";
    valorSensor=0;
    digitalWrite(led, HIGH);
    delay(10);
    digitalWrite(led, LOW);
    Serial.println("SENSOR ESTÁ EM :"+sensorStatus);
  }

  /* Ao fim de cada loop, o Arduino transmite uma string
   *  representando o valor armazenado no contador, seguido
   *  de uma quebra de linha. Essa string será enviada para o
   *  módulo HC-06 HC-05  e daí para o Android.
   */

   if (sensorStatus=="on\n"){  
      serial.print("L: "+(String(valorSensor)+"*\n"));
      //serial.print('\n');
      Serial.println("SENSOR-----:"+String(valorSensor));
      Serial.println("**VALOR DO CONTADOR **********"+String(counter)+"*******VALOR DO CONTADOR*****************");
   }  
    serial.print(String(counter));
    serial.print('\n');
   
  /* Finalmente, incrementamos o contador e limpamos data.
   */
  counter = counter + 1;
  data = "";
  /* Um pequeno delay para evitar bugs estranhos.
   */
  delay(10);  // SE REDUZIR O DELAY NO MEU CASO O BUFFER FICA CHEIO E NÃO LIBERA PARA NOVAS MENSAGENS
}
