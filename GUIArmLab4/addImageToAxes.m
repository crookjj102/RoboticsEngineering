function [ imageObject ] = addImageToAxes( imageFileName, axesHandle, axesWidth )
%ADDIMAGETOAXES Adds an image to an axes
%   Opens the image filename and adds it to the axes
% Return the image object
% if axesWidth = 0 then use images default pixel size


% Open the file to get the imageData
% Create an image object and make the parent the axes
[imageData,map,alphaChannel] = imread(imageFileName);
imageObject = image(imageData, 'Parent', axesHandle, 'AlphaData', alphaChannel);
%imageData = imread(imageFileName);
%imageObject = image(imageData, 'Parent', axesHandle);

% Make units of the axes 'pixels'
% Visible off
set(axesHandle,'Units','Pixels','Visible','Off');

% Get the current 'Position' of the Axes so that we can use the x and y
currentPosition = get(axesHandle,'Position');


%Get the number of rows and columns of the image
[rows_height, cols_width, depth] = size(imageData);

if axesWidth == 0
    axesWidth = cols_width;
    axesHeight = rows_height;
else
    axesHeight = axesWidth * rows_height/cols_width;
end

% set the new 'Position' on the axes
set(axesHandle,'Position',[currentPosition(1) currentPosition(2) axesWidth axesHeight]);
end


