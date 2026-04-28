export type TransferStatus = 'PENDING' | 'PROCESSING' | 'APPROVED' | 'REJECTED';

export interface Transfer {
  transferId: string;
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string | null;
  clientId: string;
  status: TransferStatus;
  createdAt: string;
  updatedAt: string;
  processedAt: string | null;
  idempotencyKey: string;
}

export interface ApiResponse<T> {
  data: T;
  status: number;
  message: string;
  traceId: string;
}

export interface CreateTransferPayload {
  clientId: string;
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description?: string;
}

export interface UpdateTransferStatusPayload {
  status: TransferStatus;
}

export interface ListTransfersFilters {
  clientId: string;
  status?: TransferStatus | '';
  page: number;
  size: number;
}
