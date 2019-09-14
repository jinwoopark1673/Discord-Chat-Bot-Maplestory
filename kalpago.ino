#include "Keyboard.h"
#include "Mouse.h"

int inByte = -1;
int gameX = 0;
int gameY = 0;

enum keymouse_state
{
    DOING,
    STOPPED
};

keymouse_state state = STOPPED;

void setup() 
{
  Serial.begin(9600);
  Keyboard.begin();
  state = DOING;
}
 
void loop() {
  if (state == DOING) {
    inByte = Serial.read();
    if (inByte == 0) {
      gameX += 1;
    } else if (inByte == 1) {
      gameX += 10;
    } else if (inByte == 2) {
      gameX += 100;
    } else if (inByte == 3) {
      gameY += 1;
    } else if (inByte == 4) {
      gameY += 10;
    } else if (inByte == 5) {
      gameY += 100;
    } else if (inByte == 6) {
      Keyboard.releaseAll();
      Mouse.release();
    } else if (inByte == 7) {
      Keyboard.press(KEY_RETURN);
      delay(random(50, 80));
      Keyboard.release(KEY_RETURN);
      delay(random(50, 80));
      Keyboard.press(KEY_LEFT_CTRL);
      delay(random(50, 80));
      Keyboard.press('v');
      delay(random(50, 90));
      Keyboard.releaseAll();
      delay(random(50, 80));
      Keyboard.press(KEY_RETURN);
      delay(random(50, 80));
      Keyboard.release(KEY_RETURN);
      delay(random(50, 80));
      Keyboard.press(KEY_RETURN);
      delay(random(50, 80));
      Keyboard.release(KEY_RETURN);
      delay(random(50, 80));
    }
    if (inByte != -1) {
      Serial.write(1);
    }
  } else {
    Serial.read();
  }
}
