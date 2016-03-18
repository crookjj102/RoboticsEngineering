function [] = imageCreator( backgroundAxes, extendAxes, gripperAxes, plateLoaderObj, imageScale)
%recieves axes and the object of plateloader class
%then updates the axes to have the proper images displayed
black_block = imread('black_block.jpg','jpg');
extended_bars = imread('extended_bars.jpg','jpg');
gripper_closed_no_plate = imread('gripper_closed_no_plate.jpg','jpg');
gripper_open_no_plate = imread('gripper_open_no_plate.jpg','jpg');
gripper_with_plate = imread('gripper_with_plate.jpg','jpg');
plate_only = imread('plate_only.jpg','jpg');
robot_background = imread('robot_background','jpg');

%setup background stuff
image(robot_background, 'Parent', backgroundAxes);
[imgHeight, imgWidth, C] = size(robot_background);
aspectRatioH_W = imgHeight/imgWidth;
axesWidth = 500 * imageScale;
axesHeight = axesWidth * aspectRatioH_W;
set(backgroundAxes, 'Units', 'Pixel','Position',[0 0 axesWidth axesHeight], 'Visible', 'off');


%setup extended stuff
image(extended_bars, 'Parent', extendAxes);
[imgHeight, imgWidth, C] = size(extended_bars);
aspectRatioH_W = imgHeight/imgWidth;
axesWidth = 50 * imageScale;
axesHeight = axesWidth * aspectRatioH_W;
if(plateLoaderObj.isZAxisExtended)
    set(extendAxes, 'Units', 'Pixel','Position',[0 0 axesWidth axesHeight], 'Visible', 'off');
else
    set(extendAxes, 'Units', 'Pixel', 'Position', [0 0 1 1], 'Visible', 'off');
end

%setup gripper stuff
if(plateLoaderObj.isGripperClosed)
    if(plateLoaderObj.isPlatePresent)
        imageLoad = gripper_with_plate;
    else
        imageLoad = gripper_closed_no_plate;
    end
else
    imageLoad = gripper_open_no_plate;
end
image(imageLoad, 'Parent', gripperAxes);
[imgHeight, imgWidth, C] = size(imageLoad);
aspectRatioH_W = imgHeight/imgWidth;
axesWidth = 75 * imageScale;
axesHeight = axesWidth * aspectRatioH_W;
set(gripperAxes, 'Units', 'Pixel','Position',[0 0 axesWidth axesHeight], 'Visible', 'off');


end

