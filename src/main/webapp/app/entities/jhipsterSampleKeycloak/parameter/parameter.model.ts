export interface IParameter {
  id?: number;
  label?: string | null;
  activated?: boolean | null;
  lib2?: string | null;
  lib3?: string | null;
  refExterne?: string | null;
  val1?: string | null;
  val2?: string | null;
  val3?: string | null;
  ordre?: number | null;
  type?: IParameter | null;
  paraent?: IParameter | null;
}

export class Parameter implements IParameter {
  constructor(
    public id?: number,
    public label?: string | null,
    public activated?: boolean | null,
    public lib2?: string | null,
    public lib3?: string | null,
    public refExterne?: string | null,
    public val1?: string | null,
    public val2?: string | null,
    public val3?: string | null,
    public ordre?: number | null,
    public type?: IParameter | null,
    public paraent?: IParameter | null
  ) {
    this.activated = this.activated ?? false;
  }
}

export function getParameterIdentifier(parameter: IParameter): number | undefined {
  return parameter.id;
}
