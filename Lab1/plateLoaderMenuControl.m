function plateLoaderMenuControl (s)
running = true;
fprintf(getResponse(s));
while(running)
    switch(menu('Choose Command', 'Reset', 'X-Axis','Z-Axis','Gripper','Move','Status','Special Moves', 'Calibrate','Exit'))
        case 1
            fprintf(s,'RESET');
            fprintf(getResponse(s));
            clc;
        case 2
            fprintf(s,['X-AXIS ',num2str(menu('Choose location to move to','1','2','3','4','5'))]);
            fprintf(getResponse(s));
        case 3
            switch(menu('Z-Axis:','EXTEND','RETRACT'))
                case 1
                    fprintf(s,'Z-Axis EXTEND');
                case 2
                    fprintf(s,'Z-Axis RETRACT');
            end
            fprintf(getResponse(s));
        case 4
            switch(menu('Gripper:','OPEN','CLOSE'))
                case 1
                    fprintf(s,'GRIPPER OPEN');
                case 2
                    fprintf(s,'GRIPPER CLOSE');
            end
            fprintf(getResponse(s));
        case 5
            prompt = {'Location to pick up from','Location to move to:'};
            dlg_title = 'Move menu';
            num_lines = 1;
            defaultans = {'1','1'};
            answer = inputdlg(prompt,dlg_title,num_lines,defaultans);
            command = sprintf('MOVE %d %d',str2double(answer{1}),str2double(answer{2}));
            fprintf(s, command);
            fprintf(getResponse(s));
        case 6
            fprintf(s,'LOADER_STATUS');
            fprintf(getResponse(s));
        case 7
            switch(menu('Special Stuff','Shuffle'))
                case 1
                    uiwait(helpdlg('please place plates in position 2 and 4'));
                    fprintf(s,'GRIPPER OPEN');
                    fprintf(getResponse(s));
                    fprintf(s,'X-AXIS 2');
                    fprintf(getResponse(s));
                    fprintf(s,'Z-Axis EXTEND');
                    fprintf(getResponse(s));
                    fprintf(s,'GRIPPER CLOSE');
                    fprintf(getResponse(s));
                    fprintf(s,'Z-Axis RETRACT');
                    fprintf(getResponse(s));
                    fprintf(s,'X-Axis 1');
                    fprintf(getResponse(s));
                    fprintf(s,'Z-Axis EXTEND');
                    fprintf(getResponse(s));
                    fprintf(s,'GRIPPER OPEN');
                    fprintf(getResponse(s));
                    fprintf(s, 'Z-Axis Retract');
                    fprintf(getResponse(s));
                    fprintf(s, 'X-Axis 4');
                    fprintf(getResponse(s));
                    fprintf(s, 'Z-Axis Extend');
                    fprintf(getResponse(s));
                    fprintf(s, 'Gripper CLOSE');
                    fprintf(getResponse(s));
                    fprintf(s, 'Z-Axis Retract');
                    fprintf(getResponse(s));
                    fprintf(s, 'X-Axis 2');
                    fprintf(getResponse(s));
                    fprintf(s, 'Z-Axis Extend');
                    fprintf(getResponse(s));
                    fprintf(s, 'Gripper Open');
                    fprintf(getResponse(s));
                    fprintf(s, 'Z-Axis Retract');
                    fprintf(getResponse(s));
                    fprintf(s, 'RESET');
                    fprintf(getResponse(s));
            end
        case 8
            switch(menu('Calibration','1-2','3-2','4-2','5-2','1-3','2-3','4-3','5-3','1-4','2-4','3-4','5-4'));
                case 1
                    msDelay = inputdlg('Delay for moving from 1-2 (ms)');
                    fprintf(s,' SET_DELAY 1 2 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 2
                    msDelay = inputdlg('Delay for moving from 3-2 (ms)');
                    fprintf(s, ' SET_DELAY 3 2 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 3
                    msDelay = inputdlg('Delay for moving from 4-2 (ms)');
                    fprintf(s, ' SET_DELAY 4 2 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 4
                    msDelay = inputdlg('Delay for moving from 5-2 (ms)');
                    fprintf(s, ' SET_DELAY 5 2 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 5
                    msDelay = inputdlg('Delay for moving from 1-3 (ms)');
                    fprintf(s, ' SET_DELAY 1 3 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 6
                    msDelay = inputdlg('Delay for moving from 2-3 (ms)');
                    fprintf(s, ' SET_DELAY 2 3 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 7
                    msDelay = inputdlg('Delay for moving from 4-3 (ms)');
                    fprintf(s, ' SET_DELAY 4 3 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 8
                    msDelay = inputdlg('Delay for moving from 5-3 (ms)');
                    fprintf(s, ' SET_DELAY 5 3 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 9
                    msDelay = inputdlg('Delay for moving from 1-4 (ms)');
                    fprintf(s, ' SET_DELAY 1 4 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 10
                    msDelay = inputdlg('Delay for moving from 2-4 (ms)');
                    fprintf(s, ' SET_DELAY 2 4 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 11
                    msDelay = inputdlg('Delay for moving from 3-4 (ms)');
                    fprintf(s, ' SET_DELAY 3 4 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                case 12
                    msDelay = inputdlg('Delay for moving from 5-4 (ms)');
                    fprintf(s, ' SET_DELAY 5 4 %i\n',round(str2double(msDelay{:})));
                    fprintf(getResponse(s));
                    
            end
        case 9
            fclose(s);
            running = false;
            fprintf('Exiting.\n');
            
        otherwise%fail fast, fail safe.
            fclose(s);
            running = false;
            fprintf('Exiting.\n');
            
    end
    
end



end