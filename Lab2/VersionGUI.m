function varargout = VersionGUI(varargin)
%VERSIONGUI M-file for VersionGUI.fig
%      VERSIONGUI, by itself, creates a new VERSIONGUI or raises the existing
%      singleton*.
%
%      H = VERSIONGUI returns the handle to a new VERSIONGUI or the handle to
%      the existing singleton*.
%
%      VERSIONGUI('Property','Value',...) creates a new VERSIONGUI using the
%      given property value pairs. Unrecognized properties are passed via
%      varargin to VersionGUI_OpeningFcn.  This calling syntax produces a
%      warning when there is an existing singleton*.
%
%      VERSIONGUI('CALLBACK') and VERSIONGUI('CALLBACK',hObject,...) call the
%      local function named CALLBACK in VERSIONGUI.M with the given input
%      arguments.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help VersionGUI

% Last Modified by GUIDE v2.5 17-Mar-2016 19:51:56

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @VersionGUI_OpeningFcn, ...
                   'gui_OutputFcn',  @VersionGUI_OutputFcn, ...
                   'gui_LayoutFcn',  [], ...
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


% --- Executes just before VersionGUI is made visible.
function VersionGUI_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   unrecognized PropertyName/PropertyValue pairs from the
%            command line (see VARARGIN)

% Choose default command line output for VersionGUI
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);
set(handles.VersionText, robotVersion);

% UIWAIT makes VersionGUI wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = VersionGUI_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in pushbutton1.
function pushbutton1_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
close(gcf);


% --- Executes during object creation, after setting all properties.
function VersionText_CreateFcn(hObject, eventdata, handles)
% hObject    handle to VersionText (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called
