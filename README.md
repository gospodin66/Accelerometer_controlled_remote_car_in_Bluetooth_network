### Description

Android app for driving robot car by device accelerometer in Bluetooth network. 

**Connection**: User turns on bluetooth & starts searching for devices. After discovering and connecting to the robot car, device starts listening to accelerometer events.

**Accelerometer**: On device movement detection, app encodes x,y coordinates read from accelerometer sensor to pre-defined letters and sends to receiving device. Receiving (arduino) app decodes input letters & drives motors accordingly. 

Leters coding:
```java
char cmd = '\0';
/**
 *   fw by y -
 *   b by y +
 *   l by x +
 *   r by x -
 */
/******************stop*******************/
if(x >= -2 && y <= 2){
    cmd = 's';
}
/******************fw*******************/
if((x >= 3 && x <= 5) && (y <= 3 && y >= -3)){
    cmd = 'q';
}
if((x >= 5 && x <= 7) && (y <= 3 && y >= -3)){
    cmd = 'w';
}
if((x >= 7 && x <= 10) && (y <= 3 && y >= -3)){
    cmd = 'e';
}
/********************b*********************/
if((x <= -3 && x >= -5) && (y <= 3 && y >= -3)){
    cmd = 'r';
}
if((x <= -5 && x >= -7) && (y <= 3 && y >= -3)){
    cmd = 't';
}
if((x <= -7 && x >= -10) && (y <= 3 && y >= -3)){
    cmd = 'z';
}
/********************l*********************/
if((y >= 3 && y <= 10) && (x <= 3 && x >= -3)){
    cmd = 'a';
}
/********************r*********************/
if((y <= -3 && y >= -10) && (x <= 3 && x >= -3)){
    cmd = 'd';
}
/******************fw+l*******************/
if((y >= 2 && y >= 5) && (x >= 2 && x <= 4)){
    cmd = 'f';
}
if((y >= 2 && y >= 5) && (x >= 4 && x <= 6)){
    cmd = 'g';
}
if((y >= 2 && y >= 5) && (x >= 6 && x <= 8)){
    cmd = 'h';
}
/******************fw+r*******************/
if((y <= -2 && y >= -5) && (x >= 3 && x <= 5)){
    cmd = 'j';
}
if((y <= -2 && y >= -5) && (x >= 5 && x <= 7)){
    cmd = 'k';
}
if((y <= -2 && y >= -5) && (x >= 7 && x <= 10)){
    cmd = 'l';
}
/******************b+l*******************/
if((y >= 2 && y <= 5) && (x <= -2 && x >= -4)){
    cmd = 'y';
}
if((y >= 2 && y <= 5) && (x <= -4 && x >= -6)){
    cmd = 'x';
}
if((y >= 2 && y <= 5) && (x <= -6 && x >= -8)){
    cmd = 'c';
}
/******************b+r*******************/
if((y <= -2 && y >= -5) && (x <= -3 && x >= -5)){
    cmd = 'v';
}
if((y <= -2 && y >= -5) && (x <= -5 && x >= -7)){
    cmd = 'b';
}
if((y <= -2 && y >= -5) && (x <= -7 && x >= -10)){
    cmd = 'n';
}
return cmd;
```
#### TODO
- Add receiving app code

