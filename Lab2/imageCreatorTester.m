clear all
close all
clc

n = PlateLoaderSim(3);

n.extend();
f1 = figure(1);
f2 = figure(2);
f3 = figure(3);
a1 = axes('Parent', f1);
a2 = axes('Parent', f2);
a3 = axes('Parent', f3);

imageCreator(a1, a2, a3, n, 1.5);
