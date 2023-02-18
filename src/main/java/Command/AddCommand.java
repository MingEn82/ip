package Command;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import CommandUtils.ParseInput;
import Entities.Deadline;
import Entities.Event;
import Entities.Task;
import Entities.TaskList;
import Entities.Todo;
import EntityUtils.DateParser;
import Exceptions.DukeException;
import Exceptions.EmptyArgumentException;
import Exceptions.InsufficientArgumentsException;
import Exceptions.InvalidDateException;
import Exceptions.NoDescriptionException;
import Exceptions.UnknownInputException;
import FileUtils.Storage;
import Output.UI;

/**
 * The AddCommand class parses command to create a new task and creates the task in the constructor
 * Updates storage when execute method is called
 */
public class AddCommand extends Command implements ParseInput {
    private Task addedTask;

    /**
     * Constructor of AddCommand
     * Calls parseInput method to create the task
     * @param command type of task
     * @param input input of user
     * @throws DukeException
     */
    public AddCommand(String command, String input) throws DukeException {
        parseInput(command, input);
    }

    /**
     * Adds new task to tasklist
     * Outputs task added message
     * Writes task to storage
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage) throws DukeException {
        tasks.addTask(addedTask);
        ui.taskAddedMessage(addedTask, tasks.getTasksCount());
        storage.write(tasks);
    }

    /**
     * Helper function to parse input string
     * Creates a Deadline, Event or Todo task based on command string
     * @param command type of task
     * @param input input of user
     * @throws DukeException
     */
    public void parseInput(String command, String input) throws DukeException {
        String taskDescription, startDateString, endDateString;
        int startDateIdx, endDateIdx;
        LocalDateTime startDate, endDate;

        switch (command) {
        case "todo":
            if (input.length() == command.length()) {
                throw new NoDescriptionException(command);
            }
            taskDescription = input.substring(command.length() + 1);
            this.addedTask = new Todo(taskDescription, false);
            break;

        case "deadline":
            if (input.length() == command.length()) {
                throw new NoDescriptionException(command);
            }

            startDateIdx = input.indexOf("/by ");
            if (startDateIdx == -1) {
                throw new InsufficientArgumentsException(command, "deadline [task] /by [date]");
            }

            taskDescription = input.substring(command.length() + 1, startDateIdx - 1);
            startDateString = input.substring(startDateIdx + 4);

            if (taskDescription.length() == 0 || startDateString.length() == 0) {
                String emptyArgument;
                if (taskDescription.length() == 0) {
                    emptyArgument = "Task Name";
                } else {
                    emptyArgument = "Start Date";
                }
                throw new EmptyArgumentException(emptyArgument);
            }

            try {
                startDate = DateParser.stringToDate(startDateString);
            } catch (DateTimeParseException e) {
                throw new InvalidDateException(startDateString);
            }

            this.addedTask = new Deadline(taskDescription, false, startDate);
            break;

        case "event":
            if (input.length() == command.length()) {
                throw new NoDescriptionException(command);
            }

            startDateIdx = input.indexOf("/from ");
            endDateIdx = input.indexOf("/to ");
            if (startDateIdx == -1 || endDateIdx == -1) {
                throw new InsufficientArgumentsException(command, "event [task] /from [startDate] /to [startDate]");
            }

            taskDescription = input.substring(command.length() + 1, startDateIdx - 1);
            startDateString = input.substring(startDateIdx + 6, endDateIdx - 1);
            endDateString = input.substring(endDateIdx + 4);

            if (taskDescription.length() == 0 || startDateString.length() == 0 || endDateString.length() == 0) {
                String emptyArgument;
                if (taskDescription.length() == 0) {
                    emptyArgument = "Task Name";
                } else if (startDateString.length() == 0) {
                    emptyArgument = "Start Date";
                } else {
                    emptyArgument = "End Date";
                }

                throw new EmptyArgumentException(emptyArgument);
            }

            try {
                startDate = DateParser.stringToDate(startDateString);
            } catch (DateTimeParseException e) {
                throw new InvalidDateException(startDateString);
            }
            try {
                endDate = DateParser.stringToDate(endDateString);
            } catch (DateTimeParseException e) {
                throw new InvalidDateException(endDateString);
            }

            this.addedTask = new Event(taskDescription, false, startDate, endDate);
            break;
        default:
            throw new UnknownInputException();
        }
    }
}