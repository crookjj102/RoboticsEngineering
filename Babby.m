classdef Babby < handle
    %BABBY Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        hoursPassed;
        name;
        isDead;
    end
    
    methods
        % HOW IS BABBY FORM?
        function obj = Babby (name)
            obj.name = name;
            obj.hoursPassed = 0;
            obj.isDead = false; %I'm not dead yet!
            fprintf('Hello Baby %s\n', obj.name);
        end
        
        function hourPasses(obj)
            obj.hoursPassed = obj.hoursPassed+1;
            if(obj.hoursPassed <3)
                fprintf('Baby %s is sleeping\n', obj.name);
            elseif(obj.hoursPassed<7)
                fprintf('Baby %s is CRYING uncontrollably! Feed the baby!\n', obj.name);
            else
                obj.isDead=true;
                fprintf('You STARVED %s!!! You are a horrible parent!\n', obj.name);
            end
        end
        
        function feedBaby(obj)
            if(~obj.isDead)
                obj.hoursPassed=0;
                fprintf('Thank you for feeding baby %s! Please come again!\n', obj.name);
            else
                fprintf('You STARVED %s!!! You are a horrible parent!\n', obj.name);
            end
        end
    end
    
end

