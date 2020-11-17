package com.panpawelw.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.panpawelw.DAO.RealExerciseDAO;
import com.panpawelw.DAO.RealSolutionDAO;
import com.panpawelw.DAO.RealUserDAO;
import com.panpawelw.DAO.SolutionDAO;
import com.panpawelw.misc.ValidateParameter;
import com.panpawelw.model.Solution;
import com.panpawelw.misc.DbUtils;

@WebServlet("/addeditsolution")
public class SolutionsAdminAddEdit extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SolutionDAO solutionDAO;

    public SolutionsAdminAddEdit() {
        super();
    }

    public void init() {
        if(solutionDAO == null) solutionDAO = new RealSolutionDAO(DbUtils.initDB());
    }

    public void setSolutionDAO(SolutionDAO solutionDAO) {
        this.solutionDAO = solutionDAO;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int solutionId = ValidateParameter.checkInt(idParam, "Incorrect solution Id!");
        if(solutionId < 0) getServletContext().getRequestDispatcher("/solutionsadminpanel").forward(request, response);
        if (solutionId == 0) {
            request.setAttribute("solution", new Solution(0L));
            request.setAttribute("button", "Add solution");
        } else {
            request.setAttribute("solution", solutionDAO.loadSolutionById(solutionId));
            request.setAttribute("button", "Edit solution");
        }
        getServletContext().getRequestDispatcher("/jsp/solutionsadminaddeditview.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String solutionDescription = request.getParameter("description");
        String exercise_idParam = request.getParameter("exercise_id");
        String user_idParam = request.getParameter("user_id");
        long solutionId = ValidateParameter.checkLong(idParam, "Incorrect solution Id!");
        int solutionExercise_id = ValidateParameter.checkInt(exercise_idParam, "Incorrect exercise Id!");
        long solutionUser_id = ValidateParameter.checkLong(user_idParam, "Incorrect user Id!");
        if (solutionDescription != null && !solutionDescription.equals("") && solutionExercise_id != 0 && solutionUser_id != 0 && solutionId >= 0) {
            Solution solution = new Solution();
            Date date = new Date();
            java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
            if (solutionId != 0) {
                solution = solutionDAO.loadSolutionById(solutionId);
                solution.setUpdated(sqlDate);
            } else {
                solution.setCreated(sqlDate);
            }
            solution.setDescription(solutionDescription);
            solution.setExercise_id(solutionExercise_id);
            solution.setUser_id(solutionUser_id);
            solutionDAO.saveSolutionToDB(solution);
        } else {
            request.setAttribute("errorMessage", "Solution exercise Id, user Id nor description can't be empty!");
        }
        getServletContext().getRequestDispatcher("/solutionsadminpanel").forward(request, response);
    }
}