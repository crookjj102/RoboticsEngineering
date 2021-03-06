function varargout = PlateLoaderGUI(varargin)
% PLATELOADERGUI MATLAB code for PlateLoaderGUI.fig
%      PLATELOADERGUI, by itself, creates a new PLATELOADERGUI or raises the existing
%      singleton*.
%
%      H = PLATELOADERGUI returns the handle to a new PLATELOADERGUI or the handle to
%      the existing singleton*.
%
%      PLATELOADERGUI('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in PLATELOADERGUI.M with the given input arguments.
%
%      PLATELOADERGUI('Property','Value',...) creates a new PLATELOADERGUI or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before PlateLoaderGUI_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to PlateLoaderGUI_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help PlateLoaderGUI

% Last Modified by GUIDE v2.5 18-Mar-2016 12:30:59

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @PlateLoaderGUI_OpeningFcn, ...
                   'gui_OutputFcn',  @PlateLoaderGUI_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before PlateLoaderGUI is made visible.
function PlateLoaderGUI_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to PlateLoaderGUI (see VARARGIN)
% Choose default command line output for PlateLoaderGUI
handles.output = hObject;
global robot;
global listOfCommands;

listOfCommands = 'I'; % need to reset at the beginning.
global calibrationData;
calibrationData = [
            0 60 20 30 0
            0 0 30 30 0
            0 30 0 30 0
            0 30 30 0 0
            0 30 20 60 0];
                            
robot = PlateLoaderSim('26');
set(handles.CalibrationTable, 'Data', calibrationData, 'ColumnEditable', true);

imageCreator(handles.axes_background, handles.axes_extend, handles.axes_gripper, robot, 1.1);
moveToPos(handles.axes_gripper, 5, 0, false);
% Update handles structure
guidata(hObject, handles);


% UIWAIT makes PlateLoaderGUI wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = PlateLoaderGUI_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


function updateSim(handles)
    global robot;
    [xPos,zAxis,grip,plate] = robot.getProperties();
    imageCreator(handles.axes_background, handles.axes_extend, handles.axes_gripper, robot, 1.1);
    moveToPos(handles.axes_extend, xPos, 0, true);
    if(zAxis)
        moveToPos(handles.axes_gripper, xPos, 1, false);
    else
        moveToPos(handles.axes_gripper, xPos, 0, false);
    end
    

function moveToPos(axes, pos_x, pos_y, isExtension)
    x=0;
    y=0;
    if(~isExtension)
    if(pos_y == 0)
        y = 240;
    else
        y = 100;
    end
    switch(pos_x)
        case 1
            x = 40;
        case 2
            x = 135;
        case 3
            x = 230;
        case 4
            x = 325;
        case 5
            x = 420;
    end
    else
        y = 150;
    switch(pos_x)
        case 1
            x = 55;
        case 2
            x = 150;
        case 3
            x = 245;
        case 4
            x = 340;
        case 5
            x = 435;
    end
    end
    sound(y/300);
    sound(x/500);
    moveAxes(axes, x, y);

function moveAxes(axes, x, y)
    curPos = get(axes,'Position');
    size_1 = curPos(3);
    size_2 = curPos(4);
    set(axes,'Position',[x y size_1 size_2]);



% --- Executes on button press in ConnectionButton.
function ConnectionButton_Callback(hObject, eventdata, handles)
% hObject    handle to ConnectionButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global robot;
clc
contents=get(handles.popupmenu1, 'Value');

try
    robot = PlateLoader(contents);
catch
    fprintf('That COM Port does not exist.\n');
end


% --- Executes on button press in DisconnectButton.
function DisconnectButton_Callback(hObject, eventdata, handles)
% hObject    handle to DisconnectButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global robot;
fprintf(robot.shutdown());
try
    %fclose(handles.serialPort);
    robot = PlateLoaderSim(0);
catch
    %do nothing. already closed.
    fprintf('something went wrong');
end

% --- Executes on selection change in popupmenu1.
function popupmenu1_Callback(hObject, eventdata, handles)
% hObject    handle to popupmenu1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu1 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu1


% --- Executes during object creation, after setting all properties.
function popupmenu1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popupmenu1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --------------------------------------------------------------------
function AboutMenu_Callback(hObject, eventdata, handles)
% hObject    handle to AboutMenu (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on selection change in MoveStart.
function MoveStart_Callback(hObject, eventdata, handles)
% hObject    handle to MoveStart (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns MoveStart contents as cell array
%        contents{get(hObject,'Value')} returns selected item from MoveStart


% --- Executes during object creation, after setting all properties.
function MoveStart_CreateFcn(hObject, eventdata, handles)
% hObject    handle to MoveStart (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in MoveEnd.
function MoveEnd_Callback(hObject, eventdata, handles)
% hObject    handle to MoveEnd (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns MoveEnd contents as cell array
%        contents{get(hObject,'Value')} returns selected item from MoveEnd


% --- Executes during object creation, after setting all properties.
function MoveEnd_CreateFcn(hObject, eventdata, handles)
% hObject    handle to MoveEnd (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in popupmenu5.
function popupmenu5_Callback(hObject, eventdata, handles)
% hObject    handle to popupmenu5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu5 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu5


% --- Executes during object creation, after setting all properties.
function popupmenu5_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popupmenu5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in EnqueueButton.
function EnqueueButton_Callback(hObject, eventdata, handles)
% hObject    handle to EnqueueButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global listOfCommands
log = '';
switch get(get(handles.ButtonGroup, 'SelectedObject'),'Tag')
    case 'Reset'
        log = 'Reset                 ';
        instruction = 'I';
    case 'XMove'
        pos = get(handles.popupmenu5, 'Value');
        instruction = strcat('X ', num2str(pos));
        log = strcat('Move To X = ', num2str(pos),'         _');
    case 'ExtendButton'
        instruction = 'E';
        log = 'Extend                ';
    case 'RetractButton'
        instruction = 'R';
        log = 'Retract               ';
    case 'CloseButton'
        instruction = 'C';
        log = 'Close Gripper         ';
    case 'OpenButton'
        instruction = 'O';
        log = 'Open Gripper          ';
    case 'MovePlate'
        Spos = get(handles.MoveStart, 'Value');
        Epos = get(handles.MoveEnd, 'Value');
        instruction = strcat('M', num2str(Spos));
        instruction = strcat(instruction, num2str(Epos));
        log = strcat('Move Plate From ',num2str(Spos),' to ', num2str(Epos), '__');
    case 'radiobutton_shuffle'
        log = 'Shuffle               ';
        instruction = 'O';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'X2';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'E';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'C';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'R';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'X1';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'E';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'O';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'R';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'X4';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'E';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'C';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'R';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'X2';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'E';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'O';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'R';
        listOfCommands = strcat(listOfCommands, ',',instruction);
        instruction = 'I';
    otherwise
        %do nothing; this should never happen.
        
end
listOfCommands = strcat(listOfCommands, ',',instruction);
    prev_list = get(handles.ExQueueBox,'string');
    if(isempty(prev_list))
        new_list = {log};
    else
    new_list = [prev_list; log];
    end
    set(handles.ExQueueBox,'string', new_list);
    set(handles.LogBox,'string','                      ');
    
%fprintf(handles.user.listOfCommands);
% fprintf('\n');
%handles.user.currentIndex= handles.user.currentIndex+1;


% --- Executes on button press in ExecuteButton.
function ExecuteButton_Callback(hObject, eventdata, handles)
% hObject    handle to ExecuteButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global listOfCommands;
global robot;
if(length(listOfCommands) == 0)
    return;
end
set(handles.LogBox,'string',{''});
temp = strsplit(listOfCommands,',');
for (i = 1:1:max(length(temp)))
    
    randomNum = -1 + rand()*2;
    sound(randomNum);
    sound(-1*randomNum);
    try
    if(temp{i} == '')
        fprintf('temp is empty');
        continue;
    end
    catch e
        listOfCommands = 'Q';
        %robot.reset();
        set(handles.ExQueueBox,'string','                      ');
        
    end
    log = '';
    firstLetter = temp{i}(1);
    if(strcmp('I',firstLetter))
        log = robot.reset();
    elseif(strcmp('X',firstLetter))
        pos = str2num(temp{i}(2));
        log = robot.x(pos);
    elseif(strcmp('E',firstLetter))
        log = robot.extend();
    elseif(strcmp('R',firstLetter))
        log = robot.retract();
    elseif(strcmp('C',firstLetter))
        log = robot.close();
    elseif(strcmp('O',firstLetter))
        log = robot.open();
    elseif(strcmp('M',firstLetter))
        pos1 = str2num(temp{i}(2));
        pos2 = str2num(temp{i}(3));
        log = robot.movePlate(pos1, pos2);
    end
    prev_list = get(handles.LogBox,'string');
    
%     if(isempty(prev_list))
%         new_list = {log};
%     else
    new_list = [prev_list; log];
%     end
    set(handles.LogBox,'string', new_list);
    updateSim(handles);
    pause(1);
end
set(handles.ExQueueBox,'string','                      ');
listOfCommands = 'Q';

% --- If Enable == 'on', executes on mouse press in 5 pixel border.
% --- Otherwise, executes on mouse press in 5 pixel border or over popupmenu5.
function popupmenu5_ButtonDownFcn(hObject, eventdata, handles)
% hObject    handle to popupmenu5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on selection change in ExQueueBox.
function ExQueueBox_Callback(hObject, eventdata, handles)
% hObject    handle to ExQueueBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns ExQueueBox contents as cell array
%        contents{get(hObject,'Value')} returns selected item from ExQueueBox


% --- Executes during object creation, after setting all properties.
function ExQueueBox_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ExQueueBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in LogBox.
function LogBox_Callback(hObject, eventdata, handles)
% hObject    handle to LogBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns LogBox contents as cell array
%        contents{get(hObject,'Value')} returns selected item from LogBox


% --- Executes during object creation, after setting all properties.
function LogBox_CreateFcn(hObject, eventdata, handles)
% hObject    handle to LogBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --------------------------------------------------------------------
function aboutRobot_Callback(hObject, eventdata, handles)
% hObject    handle to aboutRobot (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global robot;
version = robot.getVersion();
%version = 'Test String';
%VersionGUI(version);
uiwait(helpdlg(version));


% --- Executes when selected object is changed in ButtonGroup.
function ButtonGroup_SelectionChangedFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in ButtonGroup 
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function CalibrationTable_ButtonDownFcn(hObject, eventdata, handles)
% hObject    handle to CalibrationTable (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in CalibrateButton.
function CalibrateButton_Callback(hObject, eventdata, handles)
% hObject    handle to CalibrateButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global robot;
robot.setTimeValues(get(handles.CalibrationTable,'Data')); 


% --- Executes on button press in Bell.
function Bell_Callback(hObject, eventdata, handles)
% hObject    handle to Bell (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
    load gong.mat;
    nBits = 24;
    sound(-10*y, 5*Fs, nBits);



% --- Executes on button press in Whistle.
function Whistle_Callback(hObject, eventdata, handles)
% hObject    handle to Whistle (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
    nBits = 8;
    load handel.mat;
    sound(y, 3*Fs, nBits);


% --- Executes on button press in ResetCalib.
function ResetCalib_Callback(hObject, eventdata, handles)
% hObject    handle to ResetCalib (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global robot;
robot.resetDefaultTimes();
calibrationData = [
            0 60 20 30 0
            0 0 30 30 0
            0 30 0 30 0
            0 30 30 0 0
            0 30 20 60 0];
% --- Executes on button press in StatusButton.
function StatusButton_Callback(hObject, eventdata, handles)
% hObject    handle to StatusButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global robot;
set(handles.StatusText, 'String',robot.getStatus);