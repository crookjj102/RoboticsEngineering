function [A1, A2, A3, A4, A5] = makeHomogeneousTransformations(degreesTheta1, degreesTheta2, degreesTheta3, degreesTheta4, degreesTheta5)
%MAKEHOMOGENEOUSTRANSFORMATIONS Create the DH matrices for the arm.

 A1 = homogeneousTransformation(  90, 0.00, 0.00, degreesTheta1);
 A2 = homogeneousTransformation(  00, 3.15, 0.00, degreesTheta2);
 A3 = homogeneousTransformation(  00, 3.19, 0.00, degreesTheta3);
 A4 = homogeneousTransformation( -90, 0.00, 0.00, degreesTheta4);
 A5 = homogeneousTransformation(  00, 0.00, 7.75, degreesTheta5);

end
