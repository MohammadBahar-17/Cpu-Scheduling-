/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.cpuscheduling;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
/**
 *
 * @author bahar
 */
public class CpuScheduling {


    static class PCBProcess {
       boolean isCompleted;
       int id, waitTime, turnaroundTime, executionTime, arrivalTimestamp, remainingTime, completionTimestamp;
    }

    public static void FCFS(ArrayList<PCBProcess> processList, int contextSwitchDuration) {
        int currentTime = 0;
        System.out.println("Visualizing Gantt Chart for First Come First Served Algorithm:");
        for (PCBProcess p : processList) {
            if (currentTime != 0) {
                currentTime += contextSwitchDuration;
            }
            p.waitTime = currentTime;
            currentTime += p.executionTime;
            p.completionTimestamp = currentTime;
            p.turnaroundTime = p.completionTimestamp;
            System.out.print(" [ |" + p.waitTime + " - " + p.completionTimestamp + "| Process " + p.id + "]");
        }
        System.out.println("|");
        System.out.println("Process\t\tCompletion Time\tWait Time\tTurnaround Time");
        for (PCBProcess p : processList) {
            System.out.println("Process " + p.id + "\t\t" + p.completionTimestamp + "\t\t" + p.waitTime + "\t\t" + p.turnaroundTime);
        }

        double totalExecutionTime = 0;
        for (PCBProcess p : processList) {
            totalExecutionTime += p.executionTime;
        }
        double cpuUtilization = (totalExecutionTime / (currentTime + contextSwitchDuration * (processList.size() - 1))) * 100;
        System.out.println("CPU Utilization: " + cpuUtilization + "%");
       
    }

    public static void shortestRemainingTimeFirst(ArrayList<PCBProcess> processList, int contextSwitchDuration) {
        int currentTime = 0;
        int completed = 0;
        int lastProcessId = 0;
        int startPrintTime = 0;
        System.out.println("\nGantt Chart for Shortest Remaining Time First Algorithm:\n");
        while (completed < processList.size()) {
            int idx = -1;
            int minTime = Integer.MAX_VALUE;

            for (int i = 0; i < processList.size(); i++) {
                if (processList.get(i).arrivalTimestamp <= currentTime && !processList.get(i).isCompleted &&
                        processList.get(i).remainingTime < minTime) {
                    minTime = processList.get(i).remainingTime;
                    idx = i;
                }
            }
            if (idx == -1) {
                currentTime++;
            } else {
                if (lastProcessId != processList.get(idx).id && lastProcessId != 0) {
                    currentTime += contextSwitchDuration;
                }
                if (lastProcessId != processList.get(idx).id) {
                    if (lastProcessId != 0) {
                        System.out.print(" [ |" + startPrintTime + " - " + currentTime + "| Process " + lastProcessId + "]");
                    }
                    startPrintTime = currentTime;
                    lastProcessId = processList.get(idx).id;
                }
                processList.get(idx).remainingTime--;
                currentTime++;
                if (processList.get(idx).remainingTime == 0) {
                    processList.get(idx).completionTimestamp = currentTime;
                    processList.get(idx).turnaroundTime = currentTime - processList.get(idx).arrivalTimestamp;
                    processList.get(idx).waitTime = processList.get(idx).turnaroundTime - processList.get(idx).executionTime;
                    processList.get(idx).isCompleted = true;
                    completed++;
                }
            }
        }
        if (lastProcessId != 0) {
            System.out.print("| Process " + lastProcessId + "(" + startPrintTime + "-" + currentTime + ")");
        }
        System.out.println("|");
        System.out.println("Process\t\tCompletion Time\tWait Time\tTurnaround Time");
        for (PCBProcess p : processList) {
            System.out.println("Process " + p.id + "\t\t" + p.completionTimestamp + "\t\t" + p.waitTime + "\t\t" + p.turnaroundTime);
        }
        double totalExecutionTime = 0;
        for (PCBProcess p : processList) {
            totalExecutionTime += p.executionTime;
        }
        double cpuUtilization = (totalExecutionTime / (currentTime + contextSwitchDuration * (processList.size() - 1))) * 100;
        System.out.println("CPU Utilization: " + cpuUtilization + "%");
       
    }

    public static void roundRobin(ArrayList<PCBProcess> processList, int quantum, int contextSwitchDuration) {
        Queue<Integer> q = new LinkedList<>();
        int currentTime = 0;
        int lastProcessId = 0;
        System.out.println("\nGantt Chart for Round Robin Algorithm:\n");

        for (int i = 0; i < processList.size(); i++) {
            if (processList.get(i).arrivalTimestamp <= currentTime) {
                q.add(i);
            }
        }
        while (!q.isEmpty()) {
            int idx = q.poll();

            if (lastProcessId != 0 && lastProcessId != processList.get(idx).id) {
                currentTime += contextSwitchDuration;
            }

            int runTime = Math.min(quantum, processList.get(idx).remainingTime);
            System.out.print(" [ |" + currentTime + " - " + (currentTime + runTime) + "| Process " + processList.get(idx).id + "]");

            processList.get(idx).remainingTime -= runTime;
            currentTime += runTime;

            ArrayList<Integer> tempQueue = new ArrayList<>();
            while (!q.isEmpty()) {
                tempQueue.add(q.poll());
            }

            for (int i = 0; i < processList.size(); i++) {
                if (!processList.get(i).isCompleted && processList.get(i).arrivalTimestamp > processList.get(idx).arrivalTimestamp &&
                        processList.get(i).arrivalTimestamp <= currentTime && !tempQueue.contains(i)) {
                    tempQueue.add(i);
                }
            }
            if (processList.get(idx).remainingTime > 0) {
                tempQueue.add(idx);
            } else {
                processList.get(idx).isCompleted = true;
                processList.get(idx).completionTimestamp = currentTime;
                processList.get(idx).turnaroundTime = currentTime - processList.get(idx).arrivalTimestamp;
                processList.get(idx).waitTime = processList.get(idx).turnaroundTime - processList.get(idx).executionTime;
            }
            for (int id : tempQueue) {
                q.add(id);
            }
        }

        System.out.println("|");
        System.out.println("Process\t\tCompletion Time\tWait Time\tTurnaround Time");
        for (PCBProcess p : processList) {
            System.out.println("Process " + p.id + "\t\t" + p.completionTimestamp + "\t\t" + p.waitTime + "\t\t" + p.turnaroundTime);
        }
        double totalExecutionTime = 0;
        for (PCBProcess p : processList) {
            totalExecutionTime += p.executionTime;
        }
        double cpuUtilization = (totalExecutionTime / (currentTime + contextSwitchDuration * (processList.size() - 1))) * 100;
        System.out.println("CPU Utilization: " + cpuUtilization + "%");
      
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--CPU Scheduling Algorithms--");
        System.out.println("1 - First Come First Served");
        System.out.println("2 - Shortest Remaining Time First");
        System.out.println("3 - Round Robin");
        System.out.println("Enter Your Choice (1 or 2 or 3):");
        int option = scanner.nextInt();
        String filePath = "file.txt"; 
        File file = new File(filePath);
        try {
            Scanner fileScanner = new Scanner(file);
            int noOfProcesses = fileScanner.nextInt();
            ArrayList<PCBProcess> processList = new ArrayList<>();
            for (int i = 0; i < noOfProcesses; i++) {
                PCBProcess process = new PCBProcess();
                process.arrivalTimestamp = fileScanner.nextInt();
                process.executionTime = fileScanner.nextInt();
                process.id = i + 1;
                process.remainingTime = process.executionTime;
                process.isCompleted = false;
                processList.add(process);
            }
            int contextSwitchDuration = fileScanner.nextInt();
            int quantum = 0;
            if (option == 3) {
                quantum = fileScanner.nextInt();
            }
            switch (option) {
                case 1:
                    FCFS(processList, contextSwitchDuration);
                    break;
                case 2:
                    shortestRemainingTimeFirst(processList, contextSwitchDuration);
                    break;
                case 3:
                    roundRobin(processList, quantum, contextSwitchDuration);
                    break;
                default:
                    System.out.println(" Please select correct number");
                    break;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not open the file: ");
            e.printStackTrace();
        }
    }

}
