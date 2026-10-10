<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
        </div> <!-- End of content-wrapper -->
    </main> <!-- End of app-main -->
</div> <!-- End of app-container -->

<!-- Global Confirm Delete Modal Dialog -->
<div class="modal-overlay" id="confirmDeleteModal">
    <div class="modal-dialog">
        <div class="modal-icon">
            <i class="bi bi-trash3-fill"></i>
        </div>
        <h3 class="modal-title">Confirm Deletion</h3>
        <p class="modal-desc">
            Are you sure you want to permanently delete <strong id="modalItemName">this record</strong>? 
            This action cannot be undone.
        </p>
        <div class="modal-actions">
            <button type="button" class="btn btn-secondary" id="modalCancelDeleteBtn">Cancel</button>
            <button type="button" class="btn btn-danger" id="modalConfirmDeleteBtn">Yes, Delete</button>
        </div>
    </div>
</div>

<!-- Main Interactive JavaScript -->
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
