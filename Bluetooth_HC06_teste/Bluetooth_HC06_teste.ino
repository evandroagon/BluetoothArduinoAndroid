//Programa : Modulo Bluetooth HC-06 - Leds
//Autor : Adilson Thomsen
 
//Armazena o caracter recebido
char buf;
 
void setup()
{
  //Define os pinos dos leds como saida
  pinMode(13, OUTPUT);
  Serial.begin(9600);
}
 
void loop()
{
  while(Serial.available() >= 0)
  {
    buf = Serial.read();
    //Caso seja recebido R, acende o led vermelho
    if (buf == '1')
    {
      digitalWrite(13, HIGH);
      Serial.println("LED ligado !");
    }
    //Caso seja recebido G, acende o led verde
    if (buf == '0')
    {
      digitalWrite(13, LOW);
      Serial.println("LED desligado !");
    }
  }
}
