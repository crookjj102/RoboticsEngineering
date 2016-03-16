classdef BankAccount < handle
    %This prevents matlab from creating a new class every time the class
    %changes.
    %BANKACCOUNT Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        name;
        balance;
        interestRate;
    end
    
    methods
        %constructor
        function obj = BankAccount(name, balance, interestRate)
            obj.name = name;
            obj.balance = balance;
            obj.interestRate = interestRate;
        end
        %interestRate
        function accumulateInterest(obj)
           obj.deposit(obj.interestRate*obj.balance); 
        end
        
        %deposit
        function deposit(obj, dollaDollaBillsYall)
           obj.balance = obj.balance+dollaDollaBillsYall; 
        end
        %withdraw
        function withdraw(obj, dollaDollaBillsYall)
           obj.balance = obj.balance-dollaDollaBillsYall; 
        end
        
        %equivalent to toString (called disp)
        function disp(obj)
           fprintf('Name: %s\nBalance: %4.2f\n\n', obj.name, obj.balance); 
        end
    end
    
end

