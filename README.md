# Guided Vector Field Navigation
A demo app for my Guided Vector Field navigation system. The actual
GVF code lives in the RBGVFNavigation class. Vector2 is for basic
2D vector math. CubicBezierCurve models, as the name implies,
cubic bezier curves. Everything else in this project is to support
the demo functionality.

The demo by default runs in screensaver mode, and will display
fields for randomly generated Bezier curves. Clicking on the demo
will toggle to interactive mode, which will freeze the current
curve and allow you to see the guidance vector at the position
of your mouse. The transition between screensaver and interactive
mode is a bit buggy :/. If you want to use this code in FTC,
you should be able to just copy CubicBezierCurve, RBGVFNavigation,
and Vector2 into your code.

To actually use the navigation system, provide it with the robot's
desired path (as a Bezier curve) and the robot's current position.
It will provide a guidance vector for the robot. If you drive
 according to the guidance vector, the robot will converge onto
 the path while moving towards the end of the path. Assuming
 your odometery is accurate, you could run up and kick the robot
 during auto, and it will move back to the programmed path
 automagically.
