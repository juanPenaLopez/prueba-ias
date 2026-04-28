import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Transfer, TransferStatus } from '../../../../core/models/transfer.model';

@Component({
  selector: 'app-transfer-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transfer-list.component.html',
  styleUrl: './transfer-list.component.css'
})
export class TransferListComponent {
  @Input() transfers: Transfer[] = [];
  @Input() loading = false;
  @Input() errorMessage = '';
  @Input() page = 0;
  @Input() size = 10;

  @Output() pageChanged = new EventEmitter<number>();
  @Output() statusUpdated = new EventEmitter<{ transferId: string; status: TransferStatus }>();

  readonly statuses: TransferStatus[] = ['PENDING', 'PROCESSING', 'APPROVED', 'REJECTED'];

  prevPage(): void {
    if (this.page > 0) {
      this.pageChanged.emit(this.page - 1);
    }
  }

  nextPage(): void {
    if (this.transfers.length >= this.size) {
      this.pageChanged.emit(this.page + 1);
    }
  }

  updateStatus(transferId: string, status: string): void {
    this.statusUpdated.emit({ transferId, status: status as TransferStatus });
  }
}
