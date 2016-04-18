function varargout = RobotArm(varargin)
% ROBOTARM MATLAB code for RobotArm.fig
%      ROBOTARM, by itself, creates a new ROBOTARM or raises the existing
%      singleton*.
%
%      H = ROBOTARM returns the handle to a new ROBOTARM or the handle to
%      the existing singleton*.
%
%      ROBOTARM('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in ROBOTARM.M with the given input arguments.
%
%      ROBOTARM('Property','Value',...) creates a new ROBOTARM or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before RobotArm_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to RobotArm_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help RobotArm

% Last Modified by GUIDE v2.5 18-Apr-2016 15:05:17

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @RobotArm_OpeningFcn, ...
                   'gui_OutputFcn',  @RobotArm_OutputFcn, ...
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
end

% --- Executes just before RobotArm is made visible.
function RobotArm_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to RobotArm (see VARARGIN)

% Choose default command line output for RobotArm
handles.output = hObject;
handles.user.jointAngles = [0 90 0 -90 90];

addImageToAxes('wildThumper.png', handles.axes_thumperImg,400);

 view(handles.axes_arm, [-50 -50 50]);
 set(handles.axes_arm,'Color','None');
 axis(handles.axes_arm, [-13 10 -8 8 -3 14.5]);
 set(handles.axes_arm,'Units','Pixels','Visible','Off');  
makeLink0(handles.axes_arm, [.5 .5 .5]);  % Doesn't move. No handles needed.
% Save handles to the patch objects.
% Save references to the vertices of each patch, make points 4x1 not 3x1.
handles.user.link1Patch = makeLink1(handles.axes_arm, [.9 .9 .9]);
handles.user.link1Vertices = get(handles.user.link1Patch, 'Vertices')';
handles.user.link1Vertices(4,:) = ones(1, size(handles.user.link1Vertices,2));
handles.user.link2Patch = makeLink2(handles.axes_arm, [.9 .9 .9]);
handles.user.link2Vertices = get(handles.user.link2Patch, 'Vertices')';
handles.user.link2Vertices(4,:) = ones(1, size(handles.user.link2Vertices,2));
handles.user.link3Patch = makeLink3(handles.axes_arm, [.9 .9 .9]);
handles.user.link3Vertices = get(handles.user.link3Patch, 'Vertices')';
handles.user.link3Vertices(4,:) = ones(1, size(handles.user.link3Vertices,2));
handles.user.link4Patch = makeLink4(handles.axes_arm, [.9 .9 .9]);
handles.user.link4Vertices = get(handles.user.link4Patch, 'Vertices')';
handles.user.link4Vertices(4,:) = ones(1, size(handles.user.link4Vertices,2));
handles.user.link5Patch = makeLink5(handles.axes_arm, [.95 .95 0]);
handles.user.link5Vertices = get(handles.user.link5Patch, 'Vertices')';
handles.user.link5Vertices(4,:) = ones(1, size(handles.user.link5Vertices,2));

updateArm(hObject, handles);

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes RobotArm wait for user response (see UIRESUME)
% uiwait(handles.figure1);
end

% --- Outputs from this function are returned to the command line.
function varargout = RobotArm_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;
end

% --- Executes on slider movement.
function slider_gripper_Callback(hObject, eventdata, handles)
% hObject    handle to slider_gripper (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
end

% --- Executes during object creation, after setting all properties.
function slider_gripper_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_gripper (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
set(hObject,'Min',-25, 'Max',75);
set(hObject,'Value',50);
end

% --- Executes on slider movement.
function slider_joint1_Callback(hObject, eventdata, handles)
% hObject    handle to slider_joint1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
jointSliderChange(hObject, handles);
end

% --- Executes during object creation, after setting all properties.
function slider_joint1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
set(hObject,'Min',-90, 'Max',90);
set(hObject,'Value',0);
end

% --- Executes on slider movement.
function slider_joint2_Callback(hObject, eventdata, handles)
% hObject    handle to slider_joint2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
jointSliderChange(hObject, handles);
end

% --- Executes during object creation, after setting all properties.
function slider_joint2_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
set(hObject,'Min',0, 'Max',180);
set(hObject,'Value',90);
end

% --- Executes on slider movement.
function slider_joint3_Callback(hObject, eventdata, handles)
% hObject    handle to slider_joint3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
jointSliderChange(hObject, handles);
end

% --- Executes during object creation, after setting all properties.
function slider_joint3_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
set(hObject,'Min',-90, 'Max',90);
set(hObject,'Value',0);
end

% --- Executes on slider movement.
function slider_joint4_Callback(hObject, eventdata, handles)
% hObject    handle to slider_joint4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
jointSliderChange(hObject, handles);
end

% --- Executes during object creation, after setting all properties.
function slider_joint4_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
set(hObject,'Min',-180, 'Max',0);
set(hObject,'Value',-90);
end

% --- Executes on slider movement.
function slider_joint5_Callback(hObject, eventdata, handles)
% hObject    handle to slider_joint5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
jointSliderChange(hObject, handles);
end

% --- Executes during object creation, after setting all properties.
function slider_joint5_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
set(hObject,'Min',0, 'Max',180);
set(hObject,'Value',90);
end

function jointSliderChange(hObject, handles)
handles.user.jointAngles(1) = round(get(handles.slider_joint1, 'Value'));
handles.user.jointAngles(2) = round(get(handles.slider_joint2, 'Value'));
handles.user.jointAngles(3) = round(get(handles.slider_joint3, 'Value'));
handles.user.jointAngles(4) = round(get(handles.slider_joint4, 'Value'));
handles.user.jointAngles(5) = round(get(handles.slider_joint5, 'Value'));

jointAnglesStr = sprintf('%d  %d  %d  %d  %d', handles.user.jointAngles);
set(handles.text_current_joint_angles, 'String', jointAnglesStr);
updateArm(hObject, handles);
end


function editcomPort_Callback(hObject, eventdata, handles)
% hObject    handle to editcomPort (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of editcomPort as text
%        str2double(get(hObject,'String')) returns contents of editcomPort as a double

end

% --- Executes during object creation, after setting all properties.
function editcomPort_CreateFcn(hObject, eventdata, handles)
% hObject    handle to editcomPort (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end


% --- Executes on button press in pushbutton_open.
function pushbutton_open_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_open (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
open_ports = instrfind('Type', 'serial','Status','open');
if ~isempty(open_ports)
    fclose(open_ports);
end

fprintf('TODO: Open a serial connection to hte robot arm .\n');


guidata(hObject, handles);
end


% --- Executes on button press in pushbutton_close.
function pushbutton_close_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_close (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
open_ports = instrfind('Type', 'serial', 'Status','open');
if ~isempty(open_ports)
    fclose(open_ports);
end
end

function updateArm(hObject, handles)
[A1,A2,A3,A4,A5] = makeHomogeneousTransformations(handles.user.jointAngles(1),...
    handles.user.jointAngles(2),handles.user.jointAngles(3),handles.user.jointAngles(4),...
    handles.user.jointAngles(5));

T0_1 = A1;
T0_2 = A1 * A2;
T0_3 = A1 * A2 * A3;
T0_4 = A1 * A2 * A3 * A4;
T0_5 = A1 * A2 * A3 * A4 * A5;

% TODO: Use the T matricies to transform the patch vertices
link1verticesWRTground = T0_1 * handles.user.link1Vertices;
link2verticesWRTground = T0_2 * handles.user.link2Vertices;
link3verticesWRTground = T0_3 * handles.user.link3Vertices;
link4verticesWRTground = T0_4 * handles.user.link4Vertices;
link5verticesWRTground = T0_5 * handles.user.link5Vertices;


% TODO: Update the patches with the new vertices
set(handles.user.link1Patch,'Vertices', link1verticesWRTground(1:3,:)');
set(handles.user.link2Patch,'Vertices', link2verticesWRTground(1:3,:)');
set(handles.user.link3Patch,'Vertices', link3verticesWRTground(1:3,:)');
set(handles.user.link4Patch,'Vertices', link4verticesWRTground(1:3,:)');
set(handles.user.link5Patch,'Vertices', link5verticesWRTground(1:3,:)');


% Optional code (if you want to display the XYZ of the gripper).
% Update x, y, and z using the gripper (end effector) origin.
% dhOrigin = [0 0 0 1]';
% gripperWRTground = T0_5 * dhOrigin;
% fprintf('(%.3f, %.3f, %.3f)\n', gripperWRTground(1), gripperWRTground(2), gripperWRTground(3));

end
